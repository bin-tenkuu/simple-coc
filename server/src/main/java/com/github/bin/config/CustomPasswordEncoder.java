package com.github.bin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class CustomPasswordEncoder extends BCryptPasswordEncoder implements PasswordEncoder {
    public CustomPasswordEncoder() {
        super(BCryptVersion.$2Y, 11, new SecureRandom());
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return super.encode(rawPassword).substring(7);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return super.matches(rawPassword, "$2y$11$" + encodedPassword);
    }
}
