package com.radzkov.resource.service.notifications;

import com.google.common.collect.ListMultimap;
import com.radzkov.resource.config.FcmSettings;
import com.radzkov.resource.entity.ClothesType;
import com.radzkov.resource.entity.Subscription;
import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.SubscriptionRepository;
import com.radzkov.resource.repository.UserRepository;
import com.radzkov.resource.service.LocalizationService;
import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.requests.data.DataMulticastMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Radzkov Andrey
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DirtyClothesNotificationSchedulingService {
    private static Logger LOG = Logger.getLogger(DirtyClothesNotificationSchedulingService.class);
    private final FcmSettings fcmSettings;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DirtyClothesNotificationGroupingService notificationGroupingService;
    private final LocalizationService localizationService;
    private final MessageSource messageSource;
    private FcmClient fcmClient;

    @Value("${notification.domain:}")
    private String notificationDomain;
    @Value("${dirty.clothes.notification.enabled:false}")
    private Boolean dirtyClothesNotificationEnabled;
    @Value("${dirty.clothes.notification.deviation:0}")
    private Integer deviation;
    @Value("${dirty.clothes.notification.src-path}")
    private String srcPath;

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
        //TODO: select for update
        //TODO: cover with integration tests
        //tODO: dynamic properties
        if (BooleanUtils.isTrue(dirtyClothesNotificationEnabled)) {
            List<User> receivers = userRepository.findAllByUserOptionsReceiverIsTrue();
            ListMultimap<User, User> sendersForReceiver = notificationGroupingService.findAllSendersForEachReceiver(receivers);
            ListMultimap<User, TypeCountForSender> groupedByType = notificationGroupingService.groupByDirtyClothesTypes(sendersForReceiver);
            sendNotificationsToReceivers(groupedByType);
        }
    }

    private void sendNotificationsToReceivers(ListMultimap<User, TypeCountForSender> groupedByType) {
        groupedByType.keySet().parallelStream().forEach(receiver -> {
            List<TypeCountForSender> typeCountForSenders = groupedByType.get(receiver);
            CompletableFuture.runAsync(() ->
                    typeCountForSenders.forEach(typeCountForSender -> sendNotificationToReceiver(receiver, typeCountForSender)
                    ));
        });

    }

    private void sendNotificationToReceiver(User receiver, TypeCountForSender typeCountForSender) {
        try {
            List<String> tokens = subscriptionRepository.findAllByUser(receiver).stream().map(Subscription::getToken).collect(Collectors.toList());
            DataMulticastMessage fcmMessage = buildNotification(tokens, typeCountForSender);

            LOG.info("Sending notification to " + receiver.getUsername() + " about " + typeCountForSender.getType().getName());
            fcmClient.send(fcmMessage);
            Thread.sleep(deviation);
        } catch (InterruptedException | UnsupportedEncodingException e) {
            LOG.error(e);
        }
    }


    private DataMulticastMessage buildNotification(List<String> tokens, TypeCountForSender type) throws UnsupportedEncodingException {
//TODO: critical message if 0 clean
        FcmMessageOptions options = FcmMessageOptions.builder()
                .setTimeToLive(Duration.ofHours(2))
                .build();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Время стирки");
        //TOdO: падежи и локализация
        //TODO: pictures
        //TODO: receiver localization
        String body = localizationService.fixEncoding(messageSource.getMessage("dirty.clothes.notification." + type.getType().getName(), new Object[]{type.getUser().getUsername(), type.getType().getCleanItemCount()}, new Locale("ru")));
        notification.put("body", body);
        String image = srcPath + type.getType().getImgSrc();
        notification.put("badge", image);
        notification.put("tag", new Date().toString());
        notification.put("icon", image);
//        notification.put("image", image);
        notification.put("clickAction", notificationDomain + "/my-basket");
        notification.put("sound", notificationDomain + "/notificationSound.mp3");
        Map<String, String> data = new HashMap<>();
        data.put("action", notificationDomain + "/my-basket");
        notification.put("data", data);
        return new DataMulticastMessage(options, tokens, notification);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    static class TypeCountForSender {
        private User user;
        private ClothesType type;
    }
}
