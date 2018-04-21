package com.radzkov.resource.controller.v1;

import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.repository.ClothesItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Radzkov Andrey
 */
@RestController
public class ClothesController {
    @Autowired
    private ClothesItemRepository clothesItemRepository;

    @PostMapping("/put-clothes-to-basket")
    public void putClothes() {
        clothesItemRepository.save(new ClothesItem());
    }
}
