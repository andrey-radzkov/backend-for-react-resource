package com.radzkov.resource.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radzkov.resource.entity.Basket;
import com.radzkov.resource.entity.User;
import com.radzkov.resource.entity.UserOptions;
import com.radzkov.resource.repository.UserRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

/**
 * @author Radzkov Andrey
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@Api(description = "Controller for actions connected with user, settings")
public class AccountController {

    private UserRepository userRepository;

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


    private boolean isUserNotExists(String username) {
        User userByUsername = userRepository.findUserByUsername(username);
        return userByUsername == null;
    }

    private void saveNewUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setBasket(new Basket());
        userRepository.save(user);
    }
}
