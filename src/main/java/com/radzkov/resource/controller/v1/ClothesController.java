package com.radzkov.resource.controller.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radzkov.resource.entity.Basket;
import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.repository.BasketRepository;
import com.radzkov.resource.repository.ClothesItemRepository;
import com.radzkov.resource.service.SecurityService;

import lombok.AllArgsConstructor;

/**
 * @author Radzkov Andrey
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class ClothesController {

    private final ClothesItemRepository clothesItemRepository;
    private final BasketRepository basketRepository;
    private final SecurityService securityService;

    @PostMapping("/put-clothes-to-basket")
    public void putClothes() {
        //TODO: get type from request, validate if no clothes by type
        String socks = "socks";
        String username = securityService.getUsernameFromAuthentication();
        ClothesItem clothesItem = clothesItemRepository
                .findFirstByOwnerUsernameAndTypeTypeAndBasketIsNull(username, socks);
        if (clothesItem != null) {
            Basket myBasket = basketRepository.findBasketByBasketOwnersUsername(username);
            clothesItem.setBasket(myBasket);
            clothesItemRepository.save(clothesItem);
        }
    }

    @GetMapping("/all-clothes")
    public List<ClothesItem> getAllClothes() {

        String username = securityService.getUsernameFromAuthentication();
        return clothesItemRepository.findAllByOwnerUsername(username);
    }

    @GetMapping("/clean-clothes")
    public List<ClothesItem> getCleanClothes(@AuthenticationPrincipal Object principal) {

        String username = securityService.getUsernameFromAuthentication();
        return clothesItemRepository.findAllByOwnerUsernameAndBasketIsNull(username);
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
