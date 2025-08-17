package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.config.JwtUtil;
import com.ecommerce.auth_service.entity.*;
import com.ecommerce.auth_service.model.request.LoginRequest;
import com.ecommerce.auth_service.model.response.LoginResponse;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.service.AuthService;
import com.ecommerce.auth_service.service.RolePermissionService;
import com.ecommerce.auth_service.service.UserRoleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final RolePermissionService rolePermissionService;

    @Override
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmailAndStatus(req.getEmail(), GeneralStatus.ACTIVE.getValue());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new MainException(GeneralError.UNAUTHENTICATED.getCode(), "invalid credentials");
        }

        String redisKey = "auth:session:" + user.getId();
        String cachedJson = redisTemplate.opsForValue().get(redisKey);

        if (cachedJson != null) {
            UserLoginCache cachedSession = fromJson(cachedJson, UserLoginCache.class);
            return new LoginResponse(cachedSession.getToken(), user);
        }

        String token = jwtUtil.generateToken(user);
        long expiration = jwtUtil.getExpiration();

        UserRedis userData = buildUserData(user);
        UserLoginCache loginCache = new UserLoginCache(token, userData);

        redisTemplate.opsForValue().set(redisKey, toJson(loginCache), expiration, TimeUnit.MILLISECONDS);

        return new LoginResponse(token, user);
    }

    private String toJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new MainException(GeneralError.INTERNAL_SERVER_ERROR.getCode(), "Failed to convert data to JSON");
        }
    }

    @Override
    public void logout(String token) {
        Long userId = jwtUtil.validateAndGetUserId(token);
        redisTemplate.delete("auth:session:" + userId);
    }

    @Override
    public void validateToken(String token) {
        Long userId = jwtUtil.validateAndGetUserId(token);
        String redisKey = "auth:session:" + userId;
        String json = redisTemplate.opsForValue().get(redisKey);

        if (json == null) {
            throw new MainException(GeneralError.UNAUTHENTICATED.getCode(), "Session not found");
        }

        UserLoginCache loginCache = fromJson(json, UserLoginCache.class);

        if (!loginCache.getToken().equals(token)) {
            throw new MainException(GeneralError.UNAUTHENTICATED.getCode(), "Invalid session token");
        }
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new MainException(GeneralError.INTERNAL_SERVER_ERROR.getCode(), "Failed to parse Redis session data");
        }
    }

    public UserRedis buildUserData(User user) {
        List<Role> userRoles = userRoleService.findRolesByUserId(user.getId());
        List<String> roleNames = userRoles.stream().map(Role::getName).collect(Collectors.toList());

        List<String> permissionNames = rolePermissionService.findPermissionsByRoleIds(userRoles.stream().map(Role::getId).collect(Collectors.toList()))
                .stream()
                .map(Permission::getName)
                .distinct()
                .collect(Collectors.toList());

        return UserRedis.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .roles(roleNames)
                .permissions(permissionNames)
                .build();
    }
}
