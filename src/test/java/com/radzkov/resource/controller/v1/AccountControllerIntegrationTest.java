package com.radzkov.resource.controller.v1;

import com.radzkov.resource.config.ResourceApplication;
import com.radzkov.resource.entity.User;
import com.radzkov.resource.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author Radzkov Andrey
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResourceApplication.class)
//TODO: create profiles
@ActiveProfiles("integration")
public class AccountControllerIntegrationTest {

    @Autowired
    private AccountController accountController;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveUserTest() {
        User user = new User();
        user.setUsername("user");
        User saved = userRepository.save(user);
        assertNotNull(saved);
    }

}
