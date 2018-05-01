package com.radzkov.resource.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author Radzkov Andrey
 */
@Service
public class LocalizationService {
    private Map<String, ResourceBundle> messages;

    @PostConstruct
    public void init() {
        //TODO: reloadable
        messages = new HashMap<>();
        messages.put("en", ResourceBundle.getBundle("messages", Locale.ENGLISH));
        messages.put("ru", ResourceBundle.getBundle("messages", new Locale("ru")));
    }

    public Map<String, String> getLocalizaion(String language) {

        ResourceBundle resourceBundle = messages.get(language);
        return resourceBundle.keySet().stream().collect(Collectors.toMap(key -> key
                , key -> fixEncoding(resourceBundle.getString(key))));
    }

    public String fixEncoding(String message) {
        try {
            return new String(message.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
