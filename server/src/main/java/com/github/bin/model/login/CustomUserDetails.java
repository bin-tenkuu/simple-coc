package com.github.bin.model.login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bin
 * @since 2023/09/12
 */
@Getter
@Setter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final String username;
    private String password;
    private String token;
    private List<GrantedAuthority> authorities = new ArrayList<>();


    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
