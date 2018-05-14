package com.radzkov.resource.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author Radzkov Andrey
 */
@Entity
@Table(name = "clothes_types")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@RequiredArgsConstructor
public class ClothesType {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @NonNull
    @NotNull
    @Column
    private String name;

    @NonNull
    @Column
    private String imgSrc;

    @NonNull
    @Transient
    private Long allItemCount;

    @NonNull
    @Transient
    private Long cleanItemCount;

    @OneToMany(mappedBy = "type")
    @JsonIgnore
    private List<ClothesItem> clothesItems;

}
