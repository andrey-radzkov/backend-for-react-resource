package demo;

import de.bytefish.fcmjava.http.options.IFcmClientSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Radzkov Andrey
 */
@ConfigurationProperties(prefix = "fcm")
@Component
public class FcmSettings implements IFcmClientSettings {
    @Value("${fcm.api.url:}")
    private String fcmApiUrl;

    @Value("${fcm.api.key:}")
    private String fcmApiKey;

    @Override
    public String getFcmUrl() {
        return fcmApiUrl;
    }

    @Override
    public String getApiKey() {
        return fcmApiKey;
    }
}
