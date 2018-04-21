package com.radzkov.resource.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Radzkov Andrey
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column
    @Getter
    @Setter
    private String username;

}
