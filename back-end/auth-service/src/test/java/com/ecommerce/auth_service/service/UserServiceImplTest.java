package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.constant.RoleEnum;
import com.ecommerce.auth_service.common.constant.VerificationCodeType;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.model.request.RegistrationUserRequest;
import com.ecommerce.auth_service.model.request.UpdateUserRequest;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.security.AuthUtil;
import com.ecommerce.auth_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private VerificationCodeService verificationCodeService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    private RegistrationUserRequest validRequest;
    private User validUser;

    @BeforeEach
    void setup() {
        validRequest = new RegistrationUserRequest();
        validRequest.setEmail("test@example.com");
        validRequest.setPassword("Password123!");
        validRequest.setConfirmPassword("Password123!");
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setGender("Male");
        validRequest.setPhoneNumber("08123456789");
        validRequest.setBirthDate("1990-01-01");

        validUser = new User();
        validUser.setId(1L);
    }

    @Test
    void shouldThrowException_whenEmailAlreadyRegistered() {
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        MainException ex = assertThrows(MainException.class, () -> userService.register(validRequest));

        assertEquals(GeneralError.VALIDATION_ERROR.getCode(), ex.getCode());
        assertEquals("email already registered", ex.getMessage());

        verify(userRepository, times(1)).existsByEmail(validRequest.getEmail());
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository, roleService, userRoleService, verificationCodeService);
    }

    @Test
    void shouldThrowException_whenPhoneNumberAlreadyRegistered() {
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(validRequest.getPhoneNumber())).thenReturn(true);

        MainException ex = assertThrows(MainException.class, () -> userService.register(validRequest));

        assertEquals(GeneralError.VALIDATION_ERROR.getCode(), ex.getCode());
        assertEquals("phone number already registered", ex.getMessage());

        verify(userRepository, times(1)).existsByEmail(validRequest.getEmail());
        verify(userRepository, times(1)).existsByPhoneNumber(validRequest.getPhoneNumber());
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository, roleService, userRoleService, verificationCodeService);
    }

    @Test
    void shouldThrowException_whenBirthDateInvalidFormat() {
        validRequest.setBirthDate("01-01-1990");
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(validRequest.getPhoneNumber())).thenReturn(false);

        MainException ex = assertThrows(MainException.class, () -> userService.register(validRequest));

        assertEquals(GeneralError.VALIDATION_ERROR.getCode(), ex.getCode());
        assertEquals("Invalid birth date format, expected yyyy-MM-dd", ex.getMessage());

        verify(userRepository, times(1)).existsByEmail(validRequest.getEmail());
        verify(userRepository, times(1)).existsByPhoneNumber(validRequest.getPhoneNumber());
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository, roleService, userRoleService, verificationCodeService);
    }

    @Test
    void shouldThrowException_whenDefaultUserRoleNotConfigured() {
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(validRequest.getPhoneNumber())).thenReturn(false);
        when(roleService.findRoleByName(RoleEnum.USER.getName())).thenReturn(null);

        MainException ex = assertThrows(MainException.class, () -> userService.register(validRequest));

        assertEquals(GeneralError.VALIDATION_ERROR.getCode(), ex.getCode());
        assertEquals("Default role 'USER' is not configured in the system", ex.getMessage());

        verify(userRepository, times(1)).existsByEmail(validRequest.getEmail());
        verify(userRepository, times(1)).existsByPhoneNumber(validRequest.getPhoneNumber());
        verify(roleService, times(1)).findRoleByName(RoleEnum.USER.getName());
        verify(userRepository, times(1)).save(any());
        verify(userRoleService, never()).saveUserRole(any(), any());
        verifyNoMoreInteractions(userRepository, roleService, userRoleService, verificationCodeService);
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(validRequest.getPhoneNumber())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(validUser);
        when(roleService.findRoleByName(RoleEnum.USER.getName())).thenReturn(new Role(1L, "User", "User role", "ACTIVE", 1L));
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encrypted-password");
        when(verificationCodeService.generateCode(VerificationCodeType.REGISTRATION, validUser.getId())).thenReturn("123456");

        userService.register(validRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(validRequest.getEmail(), savedUser.getEmail());
        assertEquals("encrypted-password", savedUser.getPassword());
        assertEquals(validRequest.getFirstName(), savedUser.getFirstName());
        assertEquals(validRequest.getLastName(), savedUser.getLastName());
        assertEquals(validRequest.getGender(), savedUser.getGender());
        assertEquals(GeneralStatus.PENDING.getValue(), savedUser.getStatus());
        assertEquals(validRequest.getPhoneNumber(), savedUser.getPhoneNumber());
        Date expectedBirthDate = new SimpleDateFormat("yyyy-MM-dd").parse(validRequest.getBirthDate());
        assertEquals(expectedBirthDate, savedUser.getBirthDate());
        verify(roleService, times(1)).findRoleByName(RoleEnum.USER.getName());
        verify(userRoleService, times(1)).saveUserRole((eq(savedUser)), any(Role.class));;
        verify(verificationCodeService, times(1)).generateCode(VerificationCodeType.REGISTRATION, validUser.getId());
    }

    @Test
    void findById_whenUserExists_shouldReturnUser() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail("test@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        User result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findById_whenUserNotFound_shouldThrowMainException() {
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        MainException exception = assertThrows(MainException.class, () -> {
            userService.findById(userId);
        });

        assertEquals(GeneralError.NOT_FOUND.getCode(), exception.getCode());
        assertEquals("user not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deactivate_whenUserIsAlreadyInactive_shouldThrowException() {
        validUser.setStatus(GeneralStatus.INACTIVE.getValue());
        when(userRepository.findById(validUser.getId())).thenReturn(Optional.of(validUser));

        try (MockedStatic<AuthUtil> mockedAuthUtil = mockStatic(AuthUtil.class)) {
            mockedAuthUtil.when(AuthUtil::getCurrentUserId).thenReturn(99L);

            MainException exception = assertThrows(MainException.class, () -> {
                userService.deactivate(validUser.getId());
            });
            assertEquals(GeneralError.VALIDATION_ERROR.getCode(), exception.getCode());
            assertEquals("inactive user", exception.getMessage());
        }
        verify(userRepository, times(1)).findById(validUser.getId());
    }

    @Test
    void deactivate_whenUserIsActive_shouldDeactivateSuccessfully() {
        validUser.setStatus(GeneralStatus.ACTIVE.getValue());
        when(userRepository.findById(validUser.getId())).thenReturn(Optional.of(validUser));

        try (MockedStatic<AuthUtil> mockedAuthUtil = mockStatic(AuthUtil.class)) {
            Long currentUserId = 99L;
            mockedAuthUtil.when(AuthUtil::getCurrentUserId).thenReturn(currentUserId);

            userService.deactivate(validUser.getId());

            assertEquals(GeneralStatus.INACTIVE.getValue(), validUser.getStatus());
            assertEquals(currentUserId, validUser.getUpdatedBy());
            assertNotNull(validUser.getUpdatedAt());
            verify(userRepository, times(1)).save(validUser);
        }
        verify(userRepository, times(1)).findById(validUser.getId());
    }

    @Test
    void requestReactivation_whenUserIsActive_shouldThrowException() {
        validUser.setStatus(GeneralStatus.ACTIVE.getValue());
        when(userRepository.findById(validUser.getId())).thenReturn(Optional.of(validUser));

        try (MockedStatic<AuthUtil> mockedAuthUtil = mockStatic(AuthUtil.class)) {
            mockedAuthUtil.when(AuthUtil::getCurrentUserId).thenReturn(99L);

            MainException exception = assertThrows(MainException.class, () -> {
                userService.requestUserReactivation(validUser.getId());
            });
            assertEquals(GeneralError.VALIDATION_ERROR.getCode(), exception.getCode());
            assertEquals("User is in ACTIVE status", exception.getMessage());
        }
        verify(userRepository, times(1)).findById(validUser.getId());
    }

    @Test
    void requestReactivation_whenUserIsInactive_shouldChangeStatusSuccessfully() {
        validUser.setStatus(GeneralStatus.INACTIVE.getValue());
        when(userRepository.findById(validUser.getId())).thenReturn(Optional.of(validUser));

        try (MockedStatic<AuthUtil> mockedAuthUtil = mockStatic(AuthUtil.class)) {
            Long currentUserId = 99L;
            mockedAuthUtil.when(AuthUtil::getCurrentUserId).thenReturn(currentUserId);
            userService.requestUserReactivation(validUser.getId());

            assertEquals(GeneralStatus.PENDING.getValue(), validUser.getStatus());
            assertEquals(currentUserId, validUser.getUpdatedBy());
            assertNotNull(validUser.getUpdatedAt());
            verify(userRepository, times(1)).save(validUser);
        }
        verify(userRepository, times(1)).findById(validUser.getId());
    }


    @Test
    void updateUser_whenUserIsInactive_shouldThrowException() {
       validUser.setStatus(GeneralStatus.INACTIVE.getValue());
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("Updated");
        when(userRepository.findById(validUser.getId())).thenReturn(Optional.of(validUser));

        try (MockedStatic<AuthUtil> mockedAuthUtil = mockStatic(AuthUtil.class)) {
            mockedAuthUtil.when(AuthUtil::getCurrentUserId).thenReturn(99L);

            MainException exception = assertThrows(MainException.class, () -> {
                userService.updateUser(validUser.getId(), updateRequest);
            });

            assertEquals(GeneralError.VALIDATION_ERROR.getCode(), exception.getCode());
            assertEquals("inactive user", exception.getMessage());
        }
        verify(userRepository, times(1)).findById(validUser.getId());
    }

    @Test
    void updateUser_shouldUpdateFieldsSuccessfully() {
        Long updaterId = 77L;

        validUser.setStatus(GeneralStatus.ACTIVE.getValue());

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("NewName");
        updateRequest.setLastName("LastName");
        updateRequest.setGender("Male");

        when(userRepository.findById(validUser.getId())).thenReturn(Optional.of(validUser));

        try (MockedStatic<AuthUtil> mockedAuthUtil = mockStatic(AuthUtil.class)) {
            mockedAuthUtil.when(AuthUtil::getCurrentUserId).thenReturn(updaterId);
            userService.updateUser(validUser.getId(), updateRequest);
        }

        assertEquals("NewName", validUser.getFirstName());
        assertEquals("LastName", validUser.getLastName());
        assertEquals("Male", validUser.getGender());
        assertEquals(updaterId, validUser.getUpdatedBy());
        assertNotNull(validUser.getUpdatedAt());
        verify(userRepository, times(1)).save(validUser);
    }
}