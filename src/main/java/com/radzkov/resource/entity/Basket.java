package com.radzkov.resource.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Radzkov Andrey
 */
@Entity
@Table(name = "basket")
@Getter
@Setter
public class Basket {
    //TODO: calculate basket size
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "basket")
    @JsonIgnore
    private List<User> basketOwners;

    @OneToMany(mappedBy = "basket", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ClothesItem> dirtyClothes;


}
