package com.radzkov.resource.repository;


import com.radzkov.resource.entity.NotificationType;

/**
 * @author Radzkov Andrey
 */
public interface NotificationTypeRepository extends EntityRepository<NotificationType> {

    NotificationType findByName(String name);
}
