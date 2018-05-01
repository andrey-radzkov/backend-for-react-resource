package com.radzkov.resource.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author Radzkov Andrey
 */
@Getter
@Setter
public class TypeQuery {
    @NotNull
    private String name;
}
