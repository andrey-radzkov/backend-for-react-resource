package com.radzkov.resource.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * @author Radzkov Andrey
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode
//TODO: save timezone
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "basket_id")
    @JsonIgnore
    private Basket basket;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "options_id")
    private UserOptions userOptions;

}
