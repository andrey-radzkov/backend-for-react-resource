package com.radzkov.resource.controller.v1;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Radzkov Andrey
 */
@RestController
public class WashClothesController {
    @PostMapping("/put-clothes")
    public void putClothes() {
        System.out.println("clothes putted");
    }
}
