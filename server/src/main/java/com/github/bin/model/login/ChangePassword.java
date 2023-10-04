package com.github.bin.model.login;

import jakarta.validation.Valid;
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
@Valid
public class ChangePassword {
    @NotBlank
    @Length(min = 1, max = 20)
    private String oldPassword;
    @NotBlank
    @Length(min = 1, max = 20)
    private String newPassword;
    @NotBlank
    @Length(min = 1, max = 20)
    private String confirmPassword;
}
