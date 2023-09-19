package com.github.bin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/9/17
 */
@Getter
@Setter
@AllArgsConstructor
public class ResultModel<T> {
    private int code;
    private String msg;
    private T data;

    public ResultModel(T data) {
        this(0, "成功", data);
    }

    public ResultModel(int code, String msg) {
        this(code, msg, null);
    }

    public static <T> ResultModel<T> success(T data) {
        return new ResultModel<>(data);
    }

    public static <T> ResultModel<T> success() {
        return new ResultModel<>(null);
    }

    public static <T> ResultModel<T> fail(int code, String msg) {
        return new ResultModel<>(code, msg);
    }

    public static <T> ResultModel<T> fail(String msg) {
        return new ResultModel<>(1, msg);
    }
}
