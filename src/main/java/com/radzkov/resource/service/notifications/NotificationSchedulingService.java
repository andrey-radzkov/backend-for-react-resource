package com.radzkov.resource.service.notifications;

import com.google.common.collect.Lists;
import com.radzkov.resource.config.FcmSettings;
import com.radzkov.resource.entity.NotificationItem;
import com.radzkov.resource.entity.Subscription;
import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.NotificationItemRepository;
import com.radzkov.resource.repository.SubscriptionRepository;
import com.radzkov.resource.service.LocalizationService;
import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.requests.data.DataMulticastMessage;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

/**
 * @author Radzkov Andrey
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class NotificationSchedulingService {
    private static Logger LOG = Logger.getLogger(NotificationSchedulingService.class);
    private final FcmSettings fcmSettings;
    private final NotificationItemRepository notificationItemRepository;

    private final SubscriptionRepository subscriptionRepository;
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

    @Scheduled(cron = "0 0 20 * * ?", zone = "Europe/Istanbul")
    public void sendAllNotifications() {
        //tODO: use batch user selection, multi node mode
        //TODO: select for update
        //TODO: cover with integration tests
        //tODO: dynamic properties
        if (BooleanUtils.isTrue(dirtyClothesNotificationEnabled)) {
            Iterable<NotificationItem> notifications = notificationItemRepository.findAll();
            Lists.newArrayList(notifications).parallelStream().forEach(this::sendNotificationToReceiver);
            notificationItemRepository.delete(notifications);
        }
    }


    private void sendNotificationToReceiver(NotificationItem notification) {
        try {
            User receiver = notification.getReceiver();
            List<String> tokens = subscriptionRepository.findAllByUser(receiver).stream().map(Subscription::getToken).collect(Collectors.toList());
            DataMulticastMessage fcmMessage = buildNotification(tokens, notification);

            LOG.info("Sending notification to " + receiver.getUsername() + " about " + notification.getType().getName());
            fcmClient.send(fcmMessage);
            Thread.sleep(deviation);
        } catch (InterruptedException | UnsupportedEncodingException e) {
            LOG.error(e);
        }
    }


    private DataMulticastMessage buildNotification(List<String> tokens, NotificationItem type) throws UnsupportedEncodingException {
//TODO: critical message if 0 clean
        FcmMessageOptions options = FcmMessageOptions.builder()
                .setTimeToLive(Duration.ofHours(2))
                .build();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Время стирки");
        //TOdO: падежи и локализация
        //TODO: pictures
        //TODO: receiver localization
        //TODO: username for user
        String body = localizationService.fixEncoding(messageSource.getMessage("notification." + type.getType().getName(), new Object[]{type.getOwner().getUsername()}, new Locale("ru")));
        notification.put("body", body);
        String image = srcPath + type.getType().getImgSrc();
        notification.put("badge", image);
        notification.put("tag", new Date().toString());
        notification.put("icon", image);
//        notification.put("image", image);
        notification.put("clickAction", notificationDomain + "/app/");
        notification.put("sound", notificationDomain + "/app/notificationSound.mp3");
        Map<String, String> data = new HashMap<>();
        data.put("action", notificationDomain + "/app/");
        notification.put("data", data);
        return new DataMulticastMessage(options, tokens, notification);
    }

}
