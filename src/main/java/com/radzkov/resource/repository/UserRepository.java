package com.radzkov.resource.repository;


import com.radzkov.resource.entity.Basket;
import com.radzkov.resource.entity.User;

import java.util.List;

/**
 * @author Radzkov Andrey
 */
public interface UserRepository extends EntityRepository<User> {
    User findUserByUsername(String username);
    List<User> findAllByUserOptionsReceiverIsTrue();
    List<User> findAllByBasketAndUserOptionsSenderIsTrueAndIdIsNot(Basket basket, Long id);
}
