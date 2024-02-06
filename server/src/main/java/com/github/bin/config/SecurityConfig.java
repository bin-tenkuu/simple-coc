package com.github.bin.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

/**
 * @author bin
 * @since 2023/09/12
 */
@Component
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        //开启跨域访问
        security.cors(cors -> cors.configurationSource(SecurityConfig::corsFilter));
        // CSRF禁用，因为不使用session
        security.csrf(AbstractHttpConfigurer::disable);
        // 基于token，所以不需要session
        security.sessionManagement(AbstractHttpConfigurer::disable);
        // 登出操作
        security.logout(AbstractHttpConfigurer::disable);
        // 登录
        security.formLogin(AbstractHttpConfigurer::disable);
        security.rememberMe(AbstractHttpConfigurer::disable);
        // 认证失败处理类
        security.exceptionHandling(AbstractHttpConfigurer::disable);
        // 过滤请求
        security.authorizeHttpRequests(requests -> {
            requests.requestMatchers("/**").permitAll();
            requests.anyRequest().permitAll();
        });
        return security.build();
    }

    private static CorsConfiguration corsFilter(HttpServletRequest request) {
        //1. 添加 CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.addAllowedMethod(CorsConfiguration.ALL);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.setAllowCredentials(true);
        return config;
    }

}
