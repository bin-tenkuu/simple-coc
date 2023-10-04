package com.github.bin.model.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/9/29
 */
@Getter
@Setter
public class LoginModel {
    @NotBlank
    private String username;
    @NotBlank
    @Length(min = 1, max = 20)
    private String password;
}
