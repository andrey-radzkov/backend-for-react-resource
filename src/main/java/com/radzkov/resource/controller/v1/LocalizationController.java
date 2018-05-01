package com.radzkov.resource.controller.v1;

import com.radzkov.resource.service.LocalizationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Radzkov Andrey
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class LocalizationController {

    private LocalizationService localizationService;

    @GetMapping("/localization/{language}")
    public Map<String, String> getLocalizaion(@PathVariable("language") String language) {
        return localizationService.getLocalizaion(language);
    }
}
