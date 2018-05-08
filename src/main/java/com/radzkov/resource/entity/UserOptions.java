package com.radzkov.resource.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Radzkov Andrey
 */
@Entity
@Table
@Getter
@Setter
//TODO notification options
public class UserOptions {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private boolean receiver;
    @Column
    private boolean sender;
}