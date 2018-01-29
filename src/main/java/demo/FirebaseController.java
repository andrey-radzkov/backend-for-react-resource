package demo;

import com.google.common.collect.Lists;
import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.requests.notification.NotificationMulticastMessage;
import de.bytefish.fcmjava.requests.notification.NotificationPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Date;
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

    @PostConstruct
    public void init() {
        fcmClient = new FcmClient(fcmSettings);
    }

    @GetMapping("/sendPushMessage/{token}")
    public void sendPushMessageToCurrentToken(@PathVariable("token") String token) {
        //tODO: make post, implement send to all tokens
        CompletableFuture.runAsync(() -> IntStream.range(0, 3).forEach(value -> {
            try {
                Thread.sleep(5000);

                // Message Options:
                FcmMessageOptions options = FcmMessageOptions.builder()
                        .setTimeToLive(Duration.ofHours(1))
                        .build();
                String body = "New connect " + value + " with MAZ activated";
                NotificationPayload payload = NotificationPayload.builder()
                        .setTitle("Buyer MTZ sent a message")
                        .setBody(body)
                        .setTag(new Date().toString())
                        .setIcon("https://kz.all.biz/img/kz/catalog/670883.jpeg")
                        .setColor("#aa0000")
                        .setClickAction("https://localhost:3000/redux-form") //TODO: domain to enviroment-dependent config
                        .setSound("https://localhost:3000/notificationSound.mp3")
                        .build();
                fcmClient.send(new NotificationMulticastMessage(options, Lists.newArrayList(token), payload));
                //TODO: to jms, handle exception
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
