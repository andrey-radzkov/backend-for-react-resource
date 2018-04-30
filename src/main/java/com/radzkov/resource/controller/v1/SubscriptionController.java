package com.radzkov.resource.controller.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.radzkov.resource.config.FcmSettings;
import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.entity.Subscription;
import com.radzkov.resource.entity.User;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Radzkov Andrey
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class SubscriptionController {
    //TODO: extract to constants
    public static final int CRITICAL_MINIMUM = 2;
    public static final int DEVIATION = 15000;
    private static Logger LOG = Logger.getLogger(SubscriptionController.class);

    private final FcmSettings fcmSettings;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ClothesItemRepository clothesItemRepository;

    private FcmClient fcmClient;

    @Value("${notification.domain:}")
    private String notificationDomain;
    @Value("${dirty.clothes.notification.enabled:false}")
    private Boolean dirtyClothesNotificationEnabled;

    private Set<String> allTokens = Sets.newHashSet();

    @PostConstruct
    public void init() {

        fcmClient = new FcmClient(fcmSettings);
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody @Valid Subscription subscription, @AuthenticationPrincipal String username) {
        User subscribedUser = userRepository.findUserByUsername(username);
        subscription.setUser(subscribedUser);
        subscriptionRepository.save(subscription);
    }

    @Scheduled(fixedRateString = "${dirty.clothes.notification.scheduling}")
    //tODO: stop
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

    @GetMapping("/send-push-message/{token}")
    public void sendPushMessageToCurrentToken(@PathVariable("token") String token) {

        allTokens.add(token);
        //tODO: make post, implement send to all tokens
        CompletableFuture.runAsync(() -> IntStream.range(0, 3).forEach(value -> {
            try {
                Thread.sleep(5000);
                sendMessage(Lists.newArrayList(token), "placeholder");
                //TODO: to jms, handle exception
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
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
