package com.radzkov.resource.controller.v1;

import com.radzkov.resource.entity.Basket;
import com.radzkov.resource.entity.User;
import com.radzkov.resource.entity.UserOptions;
import com.radzkov.resource.repository.BasketRepository;
import com.radzkov.resource.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Radzkov Andrey
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@Api(description = "Controller for actions connected with user, settings")
public class AccountController {

    private UserRepository userRepository;
    private BasketRepository basketRepository;

    @ApiOperation(value = "Registers user on login via social if this login is first and no user with this username exists")
    @GetMapping("/register-if-necessary")
    public void registerIfNeccesary(@AuthenticationPrincipal String username) {
        if (isUserNotExists(username)) {
            saveNewUser(username);
        }
    }

    @GetMapping("/get-user-settings")
    public UserOptions getUserSettings(@AuthenticationPrincipal String username) {
        return userRepository.findUserByUsername(username).getUserOptions();
    }

    @PostMapping("/save-user-settings")
    public void saveUserSettings(@RequestBody UserOptions userOptions, @AuthenticationPrincipal String username) {
        User currentUser = userRepository.findUserByUsername(username);
        currentUser.setUserOptions(userOptions);
        userRepository.save(currentUser);
    }

    //TODO: rename tp receiverUsername
    @PostMapping("/save-receiver")
    public void saveReceiver(@RequestParam String receiverId, @AuthenticationPrincipal String username) {
        User currentUser = userRepository.findUserByUsername(username);
        User receiver = userRepository.findUserByUsername(receiverId);
        Basket receiversBasket = basketRepository.findBasketByBasketOwnersUsername(receiverId);
        //TODO: validate if user already in application
        if (receiversBasket != null && BooleanUtils.isTrue(receiver.getUserOptions().getReceiver())) {
            currentUser.setBasket(receiversBasket);
            userRepository.save(currentUser);
        }
    }

    @PostMapping("/save-senders")
    public void saveSenders(@RequestParam List<String> senderIds, @AuthenticationPrincipal String username) {
        Basket currentBasket = basketRepository.findBasketByBasketOwnersUsername(username);
        List<User> senders = userRepository.findAllByUsernameIn(senderIds);
        //TODO: validate if user already in application and receiver
        senders.forEach(sender -> {
            if (BooleanUtils.isTrue(sender.getUserOptions().getSender())) {
                sender.setBasket(currentBasket);
                userRepository.save(sender);
            }
        });
    }


    private boolean isUserNotExists(String username) {
        User userByUsername = userRepository.findUserByUsername(username);
        return userByUsername == null;
    }

    private void saveNewUser(String username) {
        User user = new User();
        user.setUsername(username);
        Basket basket = basketRepository.save(new Basket());
        user.setBasket(basket);
        userRepository.save(user);
    }
}
