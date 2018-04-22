package com.radzkov.resource.controller.v1;

import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.UserRepository;
import com.radzkov.resource.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Radzkov Andrey
 */
@RestController
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
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
