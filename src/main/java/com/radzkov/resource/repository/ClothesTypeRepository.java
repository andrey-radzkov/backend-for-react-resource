package com.radzkov.resource.repository;


import com.radzkov.resource.entity.ClothesType;

/**
 * @author Radzkov Andrey
 */
public interface ClothesTypeRepository extends EntityRepository<ClothesType> {
    ClothesType findFirstByName(String name);
}
