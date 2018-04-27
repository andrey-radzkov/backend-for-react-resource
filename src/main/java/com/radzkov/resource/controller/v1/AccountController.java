package com.radzkov.resource.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.UserRepository;
import com.radzkov.resource.service.SecurityService;

import lombok.AllArgsConstructor;

/**
 * @author Radzkov Andrey
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class AccountController {

    private UserRepository userRepository;
    private SecurityService securityService;

    @GetMapping("/register-if-necessary")
    public void registerIfNeccesary() {

        String username = securityService.getUsernameFromAuthentication();
        if (isUserNotExists(username)) {
            saveNewUser(username);
        }
    }


    private boolean isUserNotExists(String username) {

        User userByUsername = userRepository.findUserByUsername(username);
        return userByUsername == null;
    }

    private void saveNewUser(String username) {

        User user = new User();
        user.setUsername(username);
        userRepository.save(user);
    }
}
