package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.Permission;
import com.ecommerce.auth_service.model.request.SavePermissionRequest;
import com.ecommerce.auth_service.repository.PermissionRepository;
import com.ecommerce.auth_service.service.impl.PermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceImplTest {
    @Mock
    private PermissionRepository permissionRepository;
    @InjectMocks
    private PermissionServiceImpl permissionService;
    private Permission defaultPermission;

    @BeforeEach
    void setUp() {
        defaultPermission = Permission.builder()
                .id(1L)
                .name("user:read")
                .description("Read user data")
                .status(GeneralStatus.ACTIVE.getValue())
                .build();
    }

    @Test
    void shouldReturnAllPermissions_whenDataExists() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(defaultPermission);
        when(permissionRepository.findAll()).thenReturn(permissions);

        List<Permission> result = permissionService.findPermissions();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(permissionRepository).findAll();
    }

    @Test
    void shouldReturnPermission_whenPermissionExists() {
        when(permissionRepository.findByIdAndStatus(1L, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(defaultPermission);

        Permission result = permissionService.findPermission(1L);

        assertNotNull(result);
        assertEquals("user:read", result.getName());
        verify(permissionRepository).findByIdAndStatus(1L, GeneralStatus.ACTIVE.getValue());
    }

    @Test
    void shouldThrowException_whenPermissionNotFound() {
        when(permissionRepository.findByIdAndStatus(99L, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(null);

        MainException exception = assertThrows(MainException.class, () -> {
            permissionService.findPermission(99L);
        });

        assertEquals("Permission not found", exception.getMessage());
        assertEquals(GeneralError.VALIDATION_ERROR.getCode(), exception.getCode());
        verify(permissionRepository).findByIdAndStatus(99L, GeneralStatus.ACTIVE.getValue());
    }

    @Test
    void shouldSavePermission_whenRequestIsValid() {
        SavePermissionRequest request = SavePermissionRequest.builder()
                .name("user:create")
                .description("Create user")
                .build();

        when(permissionRepository.existsByName("user:create")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        permissionService.savePermission(request);

        verify(permissionRepository).save(argThat(p ->
                p.getName().equals("user:create") &&
                        p.getDescription().equals("Create user") &&
                        p.getStatus().equals(GeneralStatus.ACTIVE.getValue())
        ));
    }

    @Test
    void shouldThrowException_whenPermissionNameAlreadyExists() {
        SavePermissionRequest request = SavePermissionRequest.builder()
                .name("user:read")
                .description("duplicate")
                .build();

        when(permissionRepository.existsByName("user:read")).thenReturn(true);

        MainException exception = assertThrows(MainException.class, () -> {
            permissionService.savePermission(request);
        });

        assertEquals("Permission name already exists", exception.getMessage());
        assertEquals(GeneralError.VALIDATION_ERROR.getCode(), exception.getCode());
        verify(permissionRepository, never()).save(any());
    }

    @Test
    void shouldUpdatePermission_whenNameChangedAndNotExists() {
        SavePermissionRequest request = SavePermissionRequest.builder()
                .name("user:update")
                .description("Update user info")
                .build();

        when(permissionRepository.findByIdAndStatus(1L, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(defaultPermission);
        when(permissionRepository.existsByName("user:update")).thenReturn(false);

        permissionService.updatePermission(request, 1L);

        verify(permissionRepository).existsByName("user:update");
        verify(permissionRepository).save(argThat(p ->
                p.getName().equals("user:update") &&
                        p.getDescription().equals("Update user info")));
    }

    @Test
    void shouldUpdatePermission_whenNameIsSame_ignoreValidation() {
        SavePermissionRequest request = SavePermissionRequest.builder()
                .name("user:read")
                .description("Updated desc")
                .build();

        when(permissionRepository.findByIdAndStatus(1L, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(defaultPermission);

        permissionService.updatePermission(request, 1L);

        verify(permissionRepository, never()).existsByName(any());
        verify(permissionRepository).save(argThat(p ->
                p.getName().equals("user:read") &&
                        p.getDescription().equals("Updated desc")));
    }

    @Test
    void shouldThrowException_whenPermissionNotFoundOnUpdate() {
        SavePermissionRequest request = SavePermissionRequest.builder()
                .name("missing")
                .description("desc")
                .build();

        when(permissionRepository.findByIdAndStatus(999L, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(null);

        MainException exception = assertThrows(MainException.class, () -> {
            permissionService.updatePermission(request, 999L);
        });

        assertEquals("Permission not found", exception.getMessage());
        verify(permissionRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenNewPermissionNameAlreadyExists() {
        SavePermissionRequest request = SavePermissionRequest.builder()
                .name("existing")
                .description("desc")
                .build();

        Permission existing = Permission.builder()
                .id(1L)
                .name("old-name")
                .description("old")
                .status(GeneralStatus.ACTIVE.getValue())
                .build();

        when(permissionRepository.findByIdAndStatus(1L, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(existing);
        when(permissionRepository.existsByName("existing")).thenReturn(true);

        MainException exception = assertThrows(MainException.class, () -> {
            permissionService.updatePermission(request, 1L);
        });

        assertEquals("Permission name already exists", exception.getMessage());
        verify(permissionRepository, never()).save(any());
    }

}
