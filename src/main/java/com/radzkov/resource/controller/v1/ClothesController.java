package com.radzkov.resource.controller.v1;

import com.radzkov.resource.dto.TypeQuery;
import com.radzkov.resource.entity.Basket;
import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.repository.BasketRepository;
import com.radzkov.resource.repository.ClothesItemRepository;
import com.radzkov.resource.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Radzkov Andrey
 */

@RestController
public class ClothesController {
    @Autowired
    private ClothesItemRepository clothesItemRepository;
    @Autowired
    private BasketRepository basketRepository;
    @Autowired
    private SecurityService securityService;

    @PostMapping(value = "/put-clothes-to-basket")
    public void putClothes(@RequestBody @Valid TypeQuery type) {
        String username = securityService.getUsernameFromAuthentication();
        ClothesItem clothesItem = clothesItemRepository.findFirstByOwnerUsernameAndTypeTypeAndBasketIsNull(username, type.getType());
        if (clothesItem != null) {
            Basket myBasket = basketRepository.findBasketByBasketOwnersUsername(username);
            clothesItem.setBasket(myBasket);
            clothesItemRepository.save(clothesItem);
        }
    }

    @GetMapping("/my-clothes")
    public List<ClothesItem> getMyClothes() {
        String username = securityService.getUsernameFromAuthentication();
        return clothesItemRepository.findAllByOwnerUsername(username);
    }

    @GetMapping("/my-basket")
    public Basket getMyBasket() {
        String username = securityService.getUsernameFromAuthentication();
        return basketRepository.findBasketByBasketOwnersUsername(username);
    }

    @PostMapping("/wash-clothes-in-basket")
    public void washClothesInBasket() {
        //TODO: read clothes types and count from query
        //TODO: уведомление о стирке другому владельцу корзины
        String username = securityService.getUsernameFromAuthentication();
        Basket myBasket = basketRepository.findBasketByBasketOwnersUsername(username);
        myBasket.getDirtyClothes().parallelStream().forEach(item -> {
            item.setBasket(null);
        });
        basketRepository.save(myBasket);
    }
}
