package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.Permission;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.RolePermission;
import com.ecommerce.auth_service.entity.RolePermissionId;
import com.ecommerce.auth_service.repository.PermissionRepository;
import com.ecommerce.auth_service.repository.RolePermissionRepository;
import com.ecommerce.auth_service.repository.RoleRepository;
import com.ecommerce.auth_service.service.impl.RolePermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RolePermissionServiceImplTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private RolePermissionRepository rolePermissionRepository;
    @InjectMocks
    private RolePermissionServiceImpl rolePermissionService;

    private Role role;
    private Permission permission1, permission2;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1L)
                .name("admin")
                .status(GeneralStatus.ACTIVE.getValue())
                .build();

        permission1 = Permission.builder()
                .id(10L)
                .name("user:read")
                .status(GeneralStatus.ACTIVE.getValue())
                .build();

        permission2 = Permission.builder()
                .id(20L)
                .name("user:write")
                .status(GeneralStatus.ACTIVE.getValue())
                .build();
    }

    @Test
    void shouldAssignPermissionsSuccessfully() {
        List<Long> requestedIds = Arrays.asList(10L, 20L);

        when(roleRepository.findRoleByIdAndStatus(1L, "ACTIVE")).thenReturn(role);
        when(rolePermissionRepository.findAllByRoleId(1L)).thenReturn(Collections.emptyList());
        when(permissionRepository.findByIdAndStatus(10L, "ACTIVE")).thenReturn(permission1);
        when(permissionRepository.findByIdAndStatus(20L, "ACTIVE")).thenReturn(permission2);

        rolePermissionService.assignPermissions(1L, requestedIds);

        ArgumentCaptor<RolePermission> captor = ArgumentCaptor.forClass(RolePermission.class);
        verify(rolePermissionRepository, times(2)).save(captor.capture());

        List<RolePermission> saved = captor.getAllValues();
        assertEquals(2, saved.size());
        assertTrue(saved.stream().anyMatch(rp -> rp.getPermission().getId().equals(10L)));
        assertTrue(saved.stream().anyMatch(rp -> rp.getPermission().getId().equals(20L)));
    }

    @Test
    void shouldRemoveUnassignedPermissions() {
        RolePermission existing = new RolePermission();
        existing.setId(new RolePermissionId(1L, 10L));
        existing.setPermission(permission1);
        existing.setRole(role);

        when(roleRepository.findRoleByIdAndStatus(1L, GeneralStatus.ACTIVE.getValue())).thenReturn(role);
        when(rolePermissionRepository.findAllByRoleId(1L)).thenReturn(Collections.singletonList(existing));
        when(permissionRepository.findByIdAndStatus(20L, GeneralStatus.ACTIVE.getValue())).thenReturn(permission2);

        rolePermissionService.assignPermissions(1L, Collections.singletonList(20L));

        verify(rolePermissionRepository).deleteById(existing.getId());
        verify(rolePermissionRepository).save(any(RolePermission.class));
    }


    @Test
    void shouldThrowException_whenRoleNotFound() {
        when(roleRepository.findRoleByIdAndStatus(999L, "ACTIVE")).thenReturn(null);

        MainException ex = assertThrows(MainException.class, () -> {
            rolePermissionService.assignPermissions(999L, Collections.singletonList(10L));
        });

        assertEquals("Role not found", ex.getMessage());
    }

    @Test
    void shouldThrowException_whenPermissionNotFound() {
        when(roleRepository.findRoleByIdAndStatus(1L, "ACTIVE")).thenReturn(role);
        when(rolePermissionRepository.findAllByRoleId(1L)).thenReturn(Collections.emptyList());
        when(permissionRepository.findByIdAndStatus(99L, "ACTIVE")).thenReturn(null);

        MainException ex = assertThrows(MainException.class, () -> {
            rolePermissionService.assignPermissions(1L, Collections.singletonList(99L));
        });

        assertEquals("Permission not found", ex.getMessage());
    }

    @Test
    void shouldDoNothing_whenPermissionAlreadyAssigned() {
        RolePermission existing = new RolePermission();
        existing.setId(new RolePermissionId(1L, 10L));
        existing.setPermission(permission1);
        existing.setRole(role);

        when(roleRepository.findRoleByIdAndStatus(1L, "ACTIVE")).thenReturn(role);
        when(rolePermissionRepository.findAllByRoleId(1L)).thenReturn(Collections.singletonList(existing));

        rolePermissionService.assignPermissions(1L, Collections.singletonList(10L));

        verify(rolePermissionRepository, never()).delete(any());
        verify(rolePermissionRepository, never()).save(any());
    }
}