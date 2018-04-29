package com.radzkov.resource.controller.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.radzkov.resource.config.FcmSettings;
import com.radzkov.resource.entity.Subscription;
import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.SubscriptionRepository;
import com.radzkov.resource.repository.UserRepository;
import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.requests.data.DataMulticastMessage;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.IntStream;

/**
 * @author Radzkov Andrey
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class SubscriptionController {

    private final FcmSettings fcmSettings;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    private FcmClient fcmClient;

    @Value("${notification.domain:}")
    private String notificationDomain;

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

    @Scheduled(fixedRateString = "${wash.required.scheduling}")
    //tODO: stop
    public void reportCurrentTime() {
        //tODO: group by user, save info about messaging to prevent duplication
        //tODO: notification in vacations
        subscriptionRepository.findAll().forEach(subscription -> {
            FcmMessageOptions options = FcmMessageOptions.builder()
                    .setTimeToLive(Duration.ofHours(2))
                    .build();
            //TODO: select other basket owners first
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", "Чистота и порядок");
            notification.put("body", "У вас закончились чистые носки");
            notification.put("badge", "https://opt-936833.ssl.1c-bitrix-cdn.ru/upload/iblock/f81/f81e13000e39381d3a42d4bcc0840f9d.png");
            notification.put("tag", new Date().toString());
            notification.put("icon", "https://opt-936833.ssl.1c-bitrix-cdn.ru/upload/iblock/f81/f81e13000e39381d3a42d4bcc0840f9d.png");
            notification.put("image", "https://opt-936833.ssl.1c-bitrix-cdn.ru/upload/iblock/f81/f81e13000e39381d3a42d4bcc0840f9d.png");
            notification.put("color", "#aa0000");
            notification.put("clickAction", notificationDomain + "/my-basket");
            notification.put("sound", notificationDomain + "/notificationSound.mp3");
            HashMap<String, String> data = new HashMap<>();
            data.put("action", notificationDomain + "/my-basket");
            notification.put("data", data);
            fcmClient.send(new DataMulticastMessage(options, Lists.newArrayList(subscription.getToken()), notification));

        });
    }

    @GetMapping("/send-push-message/{token}")
    public void sendPushMessageToCurrentToken(@PathVariable("token") String token) {

        allTokens.add(token);
        //tODO: make post, implement send to all tokens
        CompletableFuture.runAsync(() -> IntStream.range(0, 3).forEach(value -> {
            try {
                Thread.sleep(5000);
                sendMessage(fcmClient, Lists.newArrayList(token), value);
                //TODO: to jms, handle exception
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    private void sendMessage(FcmClient fcmClient, List<String> tokens, int value) {

        FcmMessageOptions options = FcmMessageOptions.builder()
                .setTimeToLive(Duration.ofHours(1))
                .build();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Buyer MTZ sent a message");
        notification.put("body", "New connect " + value + " with MAZ activated");
        notification.put("badge", "http://otv.by/uploads/posts/2011-11/1321218780_kolenval.jpg");
        notification.put("tag", new Date().toString());
        notification.put("icon", "https://kz.all.biz/img/kz/catalog/670883.jpeg");
        notification.put("image", "http://agrotorg.net/imgs/board/6/114366-1.jpg");
        notification.put("color", "#aa0000");
        notification.put("clickAction", notificationDomain + "/redux-form");
        notification.put("sound", notificationDomain + "/notificationSound.mp3");
        HashMap<String, String> data = new HashMap<>();
        data.put("action", notificationDomain + "/redux-form");
        notification.put("data", data);
        fcmClient.send(new DataMulticastMessage(options, tokens, notification));
    }


}
