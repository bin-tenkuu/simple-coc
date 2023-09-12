package com.github.bin.config;

import com.github.bin.service.login.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

/**
 * @author bin
 * @since 2023/09/12
 */
@Component
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserTokenService userTokenService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        //开启跨域访问
        security.cors();
        // CSRF禁用，因为不使用session
        security.csrf().disable();
        // 基于token，所以不需要session
        security.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 登出操作
        security.logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(userTokenService)
                .permitAll();
        // 登录
        security.formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successForwardUrl("/")
                .permitAll();
        security.rememberMe()
                .tokenValiditySeconds(60 * 60 * 24)
                .useSecureCookie(true)
                .userDetailsService(userTokenService)
                .rememberMeCookieName("REMEMBER_TOKEN")
                .alwaysRemember(true);
        security.userDetailsService(userTokenService);
        // 认证失败处理类
        security.exceptionHandling()
                .authenticationEntryPoint(userTokenService);
        // 过滤请求
        security.authorizeHttpRequests()
                .requestMatchers("/api/sys/**").permitAll()
                .anyRequest().permitAll()
        ;
        return security.build();
    }
}
