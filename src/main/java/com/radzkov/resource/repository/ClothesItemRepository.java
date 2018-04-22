package com.radzkov.resource.repository;


import com.radzkov.resource.entity.ClothesItem;

import java.util.List;

/**
 * @author Radzkov Andrey
 */
public interface ClothesItemRepository extends EntityRepository<ClothesItem> {
    List<ClothesItem> findAllByOwnerUsername(String username);
    ClothesItem findFirstByOwnerUsernameAndTypeTypeAndBasketIsNull(String username,String type);
}
