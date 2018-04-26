package com.radzkov.resource.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 * @author Radzkov Andrey
 */
@Entity
@Table(name = "clothes_types")
@Getter
@Setter
public class ClothesType {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column
    private String type;

    @Column
    private String imgSrc;


}
