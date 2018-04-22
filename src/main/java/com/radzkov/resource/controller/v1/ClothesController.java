package com.radzkov.resource.controller.v1;

import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.ClothesItemRepository;
import com.radzkov.resource.repository.UserRepository;
import com.radzkov.resource.service.SecurityService;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityService securityService;

    @PostMapping("/put-clothes-to-basket")
    public void putClothes() {
        String username = securityService.getUsernameFromAuthentication();
        ClothesItem clothesItem = new ClothesItem();
        User currentUser = userRepository.findUserByUsername(username);
        clothesItem.setOwner(currentUser);
        clothesItemRepository.save(clothesItem);
    }
}
