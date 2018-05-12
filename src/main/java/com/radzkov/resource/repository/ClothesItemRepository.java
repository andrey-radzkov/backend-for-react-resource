package com.radzkov.resource.repository;


import com.radzkov.resource.entity.ClothesItem;

import java.util.List;

/**
 * @author Radzkov Andrey
 */
public interface ClothesItemRepository extends EntityRepository<ClothesItem> {

    List<ClothesItem> findAllByOwnerUsername(String username);

    List<ClothesItem> findAllByOwnerUsernameAndBasketIsNull(String username);

    ClothesItem findFirstByOwnerUsernameAndTypeNameAndBasketIsNull(String username, String type);

    List<ClothesItem> findAllByOwnerUsernameAndTypeNameAndBasketIsNotNull(String username, String type);
}
