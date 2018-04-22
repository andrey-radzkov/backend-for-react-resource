package com.radzkov.resource.controller.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
public class LocalizationController {
    private Map<String, ResourceBundle> messages;

    @PostConstruct
    public void init() {
        //TODO: reloadable
        messages = new HashMap<>();
        messages.put("en", ResourceBundle.getBundle("messages", Locale.ENGLISH));
        messages.put("ru", ResourceBundle.getBundle("messages", new Locale("ru")));
    }

    @GetMapping("/localization/{language}")
    public Map<String, String> getLocalizaion(@PathVariable("language") String language) {
        ResourceBundle resourceBundle = messages.get(language);
        return resourceBundle.keySet().stream().collect(Collectors.toMap(key -> key
                , key -> {
                    try {
                        return new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        return messages.get("en").getString(key);
                    }
                }));
    }
}
