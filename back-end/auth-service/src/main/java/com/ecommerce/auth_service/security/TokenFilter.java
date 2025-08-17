package com.ecommerce.auth_service.security;

import com.ecommerce.auth_service.config.JwtUtil;
import com.ecommerce.auth_service.entity.UserLoginCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public TokenFilter(JwtUtil jwtUtil, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Long userId = jwtUtil.validateAndGetUserId(token);

                String redisKey = "auth:session:" + userId;
                String sessionJson = redisTemplate.opsForValue().get(redisKey);

                if (sessionJson == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired or not found");
                    return;
                }

                UserLoginCache userSession = objectMapper.readValue(sessionJson, UserLoginCache.class);

                request.setAttribute("USER_SESSION", userSession);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}