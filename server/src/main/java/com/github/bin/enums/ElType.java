package com.github.bin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bin
 * @since 2023/10/10
 */
@Getter
@AllArgsConstructor
public enum ElType {
    N(""),
    S("success"),
    W("warning"),
    I("info"),
    E("error"),
    ;
    private final String value;
}
