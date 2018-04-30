package com.radzkov.resource.controller.v1;

import com.radzkov.resource.entity.Subscription;
import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.SubscriptionRepository;
import com.radzkov.resource.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Radzkov Andrey
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class SubscriptionController {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody @Valid Subscription subscription, @AuthenticationPrincipal String username) {
        User subscribedUser = userRepository.findUserByUsername(username);
        subscription.setUser(subscribedUser);
        subscriptionRepository.save(subscription);
    }
}
