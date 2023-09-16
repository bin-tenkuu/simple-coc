package com.github.bin.config;

import com.github.bin.model.login.LoginUser;
import com.github.bin.service.RedisService;
import com.github.bin.util.IdWorker;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

/**
 * @author bin
 * @since 2023/09/12
 */
@Service
@WebFilter(filterName = "UserLoginFilter", urlPatterns = "/*")
public class UserLoginFilter implements Filter {
    private static final String AUTHORIZATION = "Authorization";
    private static final IdWorker ID_WORKER = new IdWorker(0L);
    private static final ThreadLocal<LoginUser> USER = new ThreadLocal<>();
    private static final Duration TIMEOUT = Duration.ofDays(1);

    public static LoginUser getUser() {
        return USER.get();
    }

    public static void refreshUser() {
        val user = USER.get();
        if (user != null) {
            RedisService.setValue("token:" + user.getToken(), user, TIMEOUT);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        if (servletRequest instanceof HttpServletRequest request
                && servletResponse instanceof HttpServletResponse response) {
            val cookie = new Cookie(AUTHORIZATION, "");
            val key = getToken(request);
            if (key != null) {
                cookie.setValue(key);
                var user = RedisService.getValue("token:" + key, LoginUser.class, TIMEOUT);
                var renew = false;
                if (user == null) {
                    user = new LoginUser();
                    user.setToken(key);
                    renew = true;
                }
                USER.set(user);
                checkRequest(user, renew, request);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
                cookie.setValue(Long.toString(ID_WORKER.nextId()));
            }
            cookie.setMaxAge((int) TIMEOUT.getSeconds());
            response.addCookie(cookie);
        }
        chain.doFilter(servletRequest, servletResponse);
        USER.remove();
    }

    private static @Nullable String getToken(HttpServletRequest request) {
        val cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AUTHORIZATION.equals(cookie.getName())) {
                    return (cookie.getValue());
                }
            }
        }
        return null;
    }

    private static void checkRequest(
            LoginUser user, boolean renew,
            HttpServletRequest request
    ) {
        val remoteAddr = request.getRemoteAddr();
        if (renew || !Objects.equals(user.getRemoteAddr(), remoteAddr)) {
            user.setRemoteAddr(remoteAddr);
            renew = true;
        }
        val remoteHost = request.getRemoteHost();
        if (renew || !Objects.equals(user.getRemoteHost(), remoteHost)) {
            user.setRemoteHost(remoteHost);
            renew = true;
        }
        val userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        if (renew || !Objects.equals(user.getUserAgent(), userAgent)) {
            user.setUserAgent(userAgent);
            renew = true;
        }
        if (renew) {
            RedisService.setValue("token:" + user.getToken(), user, TIMEOUT);
        }
    }

}
