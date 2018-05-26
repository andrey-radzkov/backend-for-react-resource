package com.radzkov.resource.service.notifications;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.entity.ClothesType;
import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.ClothesItemRepository;
import com.radzkov.resource.repository.UserRepository;
import com.radzkov.resource.service.notifications.DirtyClothesNotificationSchedulingService.TypeCountForSender;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Radzkov Andrey
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
class DirtyClothesNotificationGroupingService {
    //TODO: extract to options
    private static final int CRITICAL_MINIMUM = 2;
    private final UserRepository userRepository;
    private final ClothesItemRepository clothesItemRepository;
    @Value("${dirty.clothes.notification.notify-receiver-about-his-clothes:}")
    private Boolean notifyReceiverAboutHisClothes;

    //TODO: cover with integration test
    ListMultimap<User, User> findAllSendersForEachReceiver(List<User> receivers) {
        ListMultimap<User, User> receiverSenders = ArrayListMultimap.create();
        receivers.forEach(user -> {
            List<User> sendersForReceiver = userRepository
                    .findAllByBasketAndUserOptionsSenderIsTrueAndIdIsNot(user.getBasket(), user.getId());
            receiverSenders.putAll(user, sendersForReceiver);
            //TODO: if option sent to me also add receiver here, refactor
            if (BooleanUtils.isTrue(notifyReceiverAboutHisClothes)) {
                receiverSenders.put(user, user);
            }

        });
        return receiverSenders;
    }


    ListMultimap<User, TypeCountForSender> groupByDirtyClothesTypes(ListMultimap<User, User> sendersForReceiver) {
        ListMultimap<User, TypeCountForSender> groupedByType = ArrayListMultimap.create();
        sendersForReceiver.entries().parallelStream().forEach(entry -> {
            User sender = entry.getValue();
            List<ClothesItem> sendersClothes = clothesItemRepository.findAllByOwnerUsername(sender.getUsername());
            List<ClothesItem> sendersDirtyClothes = sendersClothes.parallelStream().filter(clothes -> Objects.nonNull(clothes.getBasket())).collect(Collectors.toList());
            Map<ClothesType, Long> countByTypeAll = countOfClothesByType(sendersClothes);
            Map<ClothesType, Long> countByTypeDirty = countOfClothesByType(sendersDirtyClothes);
            Map<ClothesType, Long> countByTypeClean = new HashMap<>();
            countByTypeAll.forEach((clothesType, count) -> countByTypeClean.put(clothesType, count - Optional.ofNullable(countByTypeDirty.get(clothesType)).orElse(0L)));

            countByTypeClean.forEach((type, count) -> {
                //TODO: read from options
                if (needToNotify(countByTypeAll, type, count)) {
                    groupedByType.put(entry.getKey(), new TypeCountForSender(sender, type, count));
                }
            });
        });
        return groupedByType;
    }

    private boolean needToNotify(Map<ClothesType, Long> countByTypeAll, ClothesType type, Long count) {
        boolean lowRestWithBigCount = count <= CRITICAL_MINIMUM && countByTypeAll.get(type) > CRITICAL_MINIMUM;
        boolean noRestWithSmallCount = count == 0 && countByTypeAll.get(type) <= CRITICAL_MINIMUM;
        return lowRestWithBigCount || noRestWithSmallCount;
    }

    Map<ClothesType, Long> countOfClothesByType(List<ClothesItem> clothesItems) {
        return clothesItems.stream().map(ClothesItem::getType).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
