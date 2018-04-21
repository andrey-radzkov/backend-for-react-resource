package com.radzkov.resource.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Radzkov Andrey
 */
@Entity
@Table(name = "users")//TODO: remap
public class Clothes {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;
}
