package com.github.bin.model.login;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/10/3
 */
@Getter
@Setter
@Valid
public class RegisterUser {
    @NotBlank
    private String username;
    @NotBlank
    @Length(min = 1, max = 20)
    private String password;
    @NotBlank
    @Length(min = 1, max = 20)
    private String confirmPassword;
    // @NotBlank
    private String inviteCode;
}
