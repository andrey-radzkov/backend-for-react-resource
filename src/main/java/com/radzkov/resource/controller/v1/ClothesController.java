package com.radzkov.resource.controller.v1;

import com.radzkov.resource.dto.TypeQuery;
import com.radzkov.resource.entity.Basket;
import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.repository.BasketRepository;
import com.radzkov.resource.repository.ClothesItemRepository;
import com.radzkov.resource.repository.ClothesTypeRepository;
import com.radzkov.resource.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final ClothesTypeRepository clothesTypeRepository;
    private final UserRepository userRepository;
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

    @DeleteMapping("/delete-clothes")
    public void deleteClothes(@RequestBody @Valid TypeQuery type, @AuthenticationPrincipal String username) {
        ClothesItem toDelete = clothesItemRepository.findFirstByOwnerUsernameAndTypeNameAndBasketIsNull(username, type.getName());
        if (toDelete == null) {
            throw new IllegalArgumentException("No Clean clothes to delete");
        }
        clothesItemRepository.delete(toDelete);
    }

    @PostMapping("/add-clothes")
    public ClothesItem addClothes(@RequestBody @Valid TypeQuery type, @AuthenticationPrincipal String username) {
        ClothesItem newItem = new ClothesItem();
        newItem.setType(clothesTypeRepository.findFirstByName(type.getName()));
        newItem.setOwner(userRepository.findUserByUsername(username));

        return clothesItemRepository.save(newItem);
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
    public void washClothesInBasket(@RequestBody @Valid TypeQuery type, @AuthenticationPrincipal String username) {
        //TODO: read clothes types and count from query
        //TODO: уведомление о стирке другому владельцу корзины
        List<ClothesItem> dirtyClothes = clothesItemRepository.findAllByOwnerUsernameAndTypeNameAndBasketIsNotNull(username, type.getName());
        dirtyClothes.forEach(item -> item.setBasket(null));
        clothesItemRepository.save(dirtyClothes);
    }
}
