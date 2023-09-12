package com.github.bin.service.login;

import com.github.bin.mapper.master.SysUserMapper;
import com.github.bin.model.login.CustomUserDetails;
import com.github.bin.service.RedisService;
import com.github.bin.util.IdWorker;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author bin
 * @since 2023/09/12
 */
@Service
@RequiredArgsConstructor
public class UserTokenService
        implements UserDetailsService, AuthenticationEntryPoint, LogoutSuccessHandler, AuthenticationManager,
        AuthenticationProvider {
    private static final String AUTHORIZATION = "Authorization";
    private static final IdWorker ID_WORKER = new IdWorker(0L);
    private PasswordEncoder passwordEncoder;
    private final SysUserMapper sysUserMapper;
    private final RedisService redis;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val sysUser = sysUserMapper.findByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        val details = new CustomUserDetails(sysUser.getUsername());
        details.setPassword(sysUser.getPassword());
        details.setToken(Long.toString(ID_WORKER.nextId()));
        return details;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        getToken(request);
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取前端表单中输入后返回的用户名、密码
        String userName = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        val userDetails = loadUserByUsername(userName);
        val matches = passwordEncoder.matches(password, userDetails.getPassword());
        if (!matches) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    private static String getToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (token != null) {
            return token;
        }
        return request.getParameter(AUTHORIZATION);
    }

}
