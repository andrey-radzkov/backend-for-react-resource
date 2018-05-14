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
     * left join clothes_items i on t.id=i.type_id
     * left join users u on u.id=i.user_id
     * where coalesce (u.username,'user')='user'
     * group by t.id;
     */
    @Query("select new ClothesType(type.id, type.name, type.imgSrc, count(item.type.id), count(item.type.id) - count(item.basket.id)) from ClothesType type " +
            "left join type.clothesItems as item " +
            "left join item.owner as owner " +
            "where coalesce (owner.username, :username) = :username " +
            "group by type.id")
    List<ClothesType> findAllWithAllAndDirtyCount(@Param("username") String username);

}
