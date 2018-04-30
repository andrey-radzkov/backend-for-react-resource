package com.radzkov.resource.service;

import com.radzkov.resource.config.FcmSettings;
import com.radzkov.resource.controller.v1.SubscriptionController;
import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.entity.Subscription;
import com.radzkov.resource.repository.ClothesItemRepository;
import com.radzkov.resource.repository.SubscriptionRepository;
import com.radzkov.resource.repository.UserRepository;
import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.requests.data.DataMulticastMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Radzkov Andrey
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DirtyClothesNotificationService {
    //TODO: extract to constants
    private static final int CRITICAL_MINIMUM = 2;
    private static final int DEVIATION = 15000;
    private static Logger LOG = Logger.getLogger(DirtyClothesNotificationService.class);
    private final FcmSettings fcmSettings;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ClothesItemRepository clothesItemRepository;
    private FcmClient fcmClient;

    @Value("${notification.domain:}")
    private String notificationDomain;
    @Value("${dirty.clothes.notification.enabled:false}")
    private Boolean dirtyClothesNotificationEnabled;

    @PostConstruct
    public void init() {

        fcmClient = new FcmClient(fcmSettings);
    }

    @Scheduled(fixedRateString = "${dirty.clothes.notification.scheduling}")
    public void notifyAboutDirtyClothes() {
        //tODO: group by user, save info about messaging to prevent duplication
        //tODO: notification in vacations
        //TODO: notify other users
        //tODO: use batch user selection, multi node mode
        if (BooleanUtils.isTrue(dirtyClothesNotificationEnabled)) {
            userRepository.findAll().forEach(user -> {
                List<ClothesItem> cleanClothes = clothesItemRepository.findAllByOwnerUsernameAndBasketIsNull(user.getUsername());
                Map<String, Long> countByType = cleanClothes.parallelStream().map(clothesItem -> clothesItem.getType().getType())
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                CompletableFuture.runAsync(() ->
                        countByType.forEach((key, value) -> {
                            if (value <= CRITICAL_MINIMUM) {
                                try {
                                    //TODO: group the same messages
                                    List<String> tokens = subscriptionRepository.findAllByUser(user).stream().map(Subscription::getToken).collect(Collectors.toList());
                                    LOG.info("Sending notification to " + user.getUsername() + " about " + key);
                                    sendMessage(tokens, key);
                                    Thread.sleep(DEVIATION);
                                } catch (InterruptedException e) {
                                    LOG.error(e);
                                }
                            }
                        }));
            });
        }
    }


    private void sendMessage(List<String> tokens, String type) {

        FcmMessageOptions options = FcmMessageOptions.builder()
                .setTimeToLive(Duration.ofHours(2))
                .build();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Чистота и порядок");
        //TOdO: падежи и локализация
        //TODO: pictures
        notification.put("body", "У вас закончились чистые " + type);
        notification.put("badge", "https://opt-936833.ssl.1c-bitrix-cdn.ru/upload/iblock/f81/f81e13000e39381d3a42d4bcc0840f9d.png");
        notification.put("tag", new Date().toString());
        notification.put("icon", "https://opt-936833.ssl.1c-bitrix-cdn.ru/upload/iblock/f81/f81e13000e39381d3a42d4bcc0840f9d.png");
        notification.put("image", "https://opt-936833.ssl.1c-bitrix-cdn.ru/upload/iblock/f81/f81e13000e39381d3a42d4bcc0840f9d.png");
        notification.put("color", "#aa0000");
        notification.put("clickAction", notificationDomain + "/my-basket");
        notification.put("sound", notificationDomain + "/notificationSound.mp3");
        Map<String, String> data = new HashMap<>();
        data.put("action", notificationDomain + "/my-basket");
        notification.put("data", data);
        fcmClient.send(new DataMulticastMessage(options, tokens, notification));
    }
}
