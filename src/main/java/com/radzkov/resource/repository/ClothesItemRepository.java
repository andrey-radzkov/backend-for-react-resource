package com.radzkov.resource.repository;


import java.util.List;

import com.radzkov.resource.entity.ClothesItem;

/**
 * @author Radzkov Andrey
 */
public interface ClothesItemRepository extends EntityRepository<ClothesItem> {

    List<ClothesItem> findAllByOwnerUsername(String username);

    List<ClothesItem> findAllByOwnerUsernameAndBasketIsNull(String username);

    ClothesItem findFirstByOwnerUsernameAndTypeTypeAndBasketIsNull(String username, String type);
}
