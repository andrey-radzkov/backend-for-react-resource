package com.radzkov.resource.repository;


import com.radzkov.resource.entity.ClothesType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Radzkov Andrey
 */
public interface ClothesTypeRepository extends EntityRepository<ClothesType> {
    ClothesType findFirstByName(String name);

    /**
     * Sql analogue:
     * <p>
     * select t.id, t.name, t.img_src, count(i.type_id)as count_all, count(i.type_id)-count(i.basket_id) as count_dirty from clothes_types t
     * <p>
     * left join clothes_items i on t.id=i.type_id and coalesce (i.user_id,'2')='2'
     * group by t.id;
     */
    @Query("select new ClothesType(type.id, type.name, type.imgSrc, count(item.type.id), count(item.type.id) - count(item.basket.id)) from ClothesType type " +
            "left join type.clothesItems as item with coalesce (item.owner.id, :userId) = :userId " +
            "group by type.id order by type.id")
    List<ClothesType> findAllWithAllAndDirtyCount(@Param("userId") Long userId);

}
