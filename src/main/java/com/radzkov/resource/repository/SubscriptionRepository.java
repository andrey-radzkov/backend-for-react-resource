package com.radzkov.resource.repository;


import com.radzkov.resource.entity.Subscription;
import com.radzkov.resource.entity.User;

import java.util.List;

/**
 * @author Radzkov Andrey
 */
public interface SubscriptionRepository extends EntityRepository<Subscription> {
    List<Subscription> findAllByUser(User user);
}
