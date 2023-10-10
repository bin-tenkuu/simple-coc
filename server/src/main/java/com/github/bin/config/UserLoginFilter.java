package com.github.bin.config;

import com.github.bin.model.login.LoginUser;
import com.github.bin.service.RedisService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;

/**
 * @author bin
 * @since 2023/09/12
 */
@Service
public class UserLoginFilter implements Filter {
    public static final String AUTHORIZATION = "Authorization";
    private static final Duration TIMEOUT = Duration.ofDays(1);

    public static void refreshUser(final LoginUser user) {
        if (user != null) {
            RedisService.setValue("token:" + user.getToken(), user, TIMEOUT);
        }
    }

    @Bean
    public FilterRegistrationBean<UserLoginFilter> filterRegistrationBean() {
        val registration = new FilterRegistrationBean<>(this);
        //过滤器名称
        registration.setName("UserLoginFilter");
        //拦截路径
        registration.addUrlPatterns("/api/*");
        //设置顺序
        registration.setOrder(10);
        return registration;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws ServletException, IOException {
        if (servletRequest instanceof HttpServletRequest request) {
            val token = getToken(request);
            if (token != null) {
                val user = RedisService.getValue("token:" + token, LoginUser.class, TIMEOUT);
                if (user != null) {
                    LoginUser.setUser(user);
                    refreshUser(user);
                    chain.doFilter(servletRequest, servletResponse);
                    LoginUser.remove();
                    return;
                }
            }
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    @Nullable
    private static String getToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

}
