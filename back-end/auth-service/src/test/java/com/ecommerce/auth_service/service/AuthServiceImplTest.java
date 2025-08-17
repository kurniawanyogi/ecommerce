package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.config.JwtUtil;
import com.ecommerce.auth_service.entity.*;
import com.ecommerce.auth_service.model.request.LoginRequest;
import com.ecommerce.auth_service.model.response.LoginResponse;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.service.impl.AuthServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private RolePermissionService rolePermissionService;
    @Mock
    private ValueOperations<String, String> valueOps;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void login_whenUserNotFound_shouldThrow() {
        LoginRequest request = new LoginRequest("email@test.com", "password");

        when(userRepository.findByEmailAndStatus(request.getEmail(), GeneralStatus.ACTIVE.getValue())).thenReturn(null);

        MainException exception = assertThrows(MainException.class, () -> authService.login(request));

        assertEquals(GeneralError.UNAUTHENTICATED.getCode(), exception.getCode());
        assertEquals("invalid credentials", exception.getMessage());
    }

    @Test
    void login_whenPasswordMismatch_shouldThrow() {
        LoginRequest request = new LoginRequest("email@test.com", "password");
        User user = new User();
        user.setPassword("hashed");

        when(userRepository.findByEmailAndStatus(request.getEmail(), GeneralStatus.ACTIVE.getValue())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), "hashed")).thenReturn(false);

        MainException exception = assertThrows(MainException.class, () -> authService.login(request));

        assertEquals(GeneralError.UNAUTHENTICATED.getCode(), exception.getCode());
        assertEquals("invalid credentials", exception.getMessage());
    }

    @Test
    void login_whenSessionExistsInRedis_shouldReturnCachedToken() throws Exception {
        LoginRequest request = new LoginRequest("email@test.com", "password");
        User user = new User();
        user.setId(1L);
        user.setEmail(request.getEmail());
        user.setPassword("hashed");
        user.setFirstName("John");
        user.setLastName("Doe");

        String token = "jwt.token";
        UserRedis userRedis = UserRedis.builder()
                .id(1L).email("email@test.com").fullName("John Doe")
                .roles(Collections.singletonList("User")).permissions(Collections.emptyList()).build();
        UserLoginCache cache = new UserLoginCache(token, userRedis);
        String json = new ObjectMapper().writeValueAsString(cache);

        when(userRepository.findByEmailAndStatus(request.getEmail(), GeneralStatus.ACTIVE.getValue())).thenReturn(user);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(valueOps.get("auth:session:1")).thenReturn(json);

        LoginResponse response = authService.login(request);

        assertEquals(token, response.getToken());
        assertEquals(user, response.getUser());
    }

    @Test
    void logout_shouldDeleteRedisKey() {
        String token = "jwt.token";
        Long userId = 1L;

        when(jwtUtil.validateAndGetUserId(token)).thenReturn(userId);

        authService.logout(token);

        verify(redisTemplate, times(1)).delete("auth:session:" + userId);
    }
}
