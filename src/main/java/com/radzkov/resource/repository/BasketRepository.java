package com.radzkov.resource.repository;


import com.radzkov.resource.entity.Basket;
import com.radzkov.resource.entity.User;

/**
 * @author Radzkov Andrey
 */
public interface BasketRepository extends EntityRepository<Basket> {
    Basket findBasketByBasketOwnersIs(User currentUser);
}
