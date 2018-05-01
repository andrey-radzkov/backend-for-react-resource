package com.radzkov.resource.controller.v1;

import com.radzkov.resource.dto.TypeQuery;
import com.radzkov.resource.entity.Basket;
import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.repository.BasketRepository;
import com.radzkov.resource.repository.ClothesItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Radzkov Andrey
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class ClothesController {

    private final ClothesItemRepository clothesItemRepository;
    private final BasketRepository basketRepository;

    @PutMapping("/put-clothes-to-basket")
    public void putClothes(@RequestBody @Valid TypeQuery type, @AuthenticationPrincipal String username) {
        ClothesItem clothesItem = clothesItemRepository.findFirstByOwnerUsernameAndTypeNameAndBasketIsNull(username, type.getName());
        if (clothesItem != null) {
            Basket myBasket = basketRepository.findBasketByBasketOwnersUsername(username);
            clothesItem.setBasket(myBasket);
            clothesItemRepository.save(clothesItem);
        }
    }

    @GetMapping("/all-clothes")
    public List<ClothesItem> getAllClothes(@AuthenticationPrincipal String username) {
        return clothesItemRepository.findAllByOwnerUsername(username);
    }

    @GetMapping("/clean-clothes")
    public List<ClothesItem> getCleanClothes(@AuthenticationPrincipal String username) {
        return clothesItemRepository.findAllByOwnerUsernameAndBasketIsNull(username);
    }

    @GetMapping("/my-basket")
    public Basket getMyBasket(@AuthenticationPrincipal String username) {
        return basketRepository.findBasketByBasketOwnersUsername(username);
    }

    @PostMapping("/wash-clothes-in-basket")
    public void washClothesInBasket(@AuthenticationPrincipal String username) {
        //TODO: read clothes types and count from query
        //TODO: уведомление о стирке другому владельцу корзины
        Basket myBasket = basketRepository.findBasketByBasketOwnersUsername(username);
        myBasket.getDirtyClothes().parallelStream().forEach(item -> item.setBasket(null));
        basketRepository.save(myBasket);
    }
}
