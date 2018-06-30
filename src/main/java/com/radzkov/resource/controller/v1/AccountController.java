package com.radzkov.resource.controller.v1;

import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
