package com.radzkov.resource.controller.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.radzkov.resource.config.FcmSettings;
import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.requests.data.DataMulticastMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * @author Radzkov Andrey
 */
@RestController
public class FirebaseController {

    @Autowired
    private FcmSettings fcmSettings;

    private FcmClient fcmClient;

    @Value("${notification.domain:}")
    private String notificationDomain;

    private Set<String> allTokens = Sets.newHashSet();

    @PostConstruct
    public void init() {
        fcmClient = new FcmClient(fcmSettings);
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

    @GetMapping("/send-push-message-to-all/")
    public void sendPushMessageToCurrentToken() {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
                sendMessage(fcmClient, Lists.newArrayList(allTokens), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
