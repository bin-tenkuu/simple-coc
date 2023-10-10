package com.github.bin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bin
 * @since 2023/10/10
 */
@Getter
@AllArgsConstructor
public enum ElPosition {
    TR("top-right"),
    TL("top-left"),
    BR("bottom-right"),
    BL("bottom-left"),
    ;
    private final String value;
}
