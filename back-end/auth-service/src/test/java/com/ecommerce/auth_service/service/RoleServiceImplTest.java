package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.model.request.SaveRoleRequest;
import com.ecommerce.auth_service.repository.RoleRepository;
import com.ecommerce.auth_service.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void shouldSaveRole_whenRequestIsValid() {
        SaveRoleRequest request = SaveRoleRequest.builder()
                .name("admin")
                .description("admin role")
                .build();

        when(roleRepository.save(any(Role.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        roleService.saveRole(request);

        verify(roleRepository).save(argThat(role ->
                role.getName().equals("admin") &&
                        role.getDescription().equals("admin role") &&
                        role.getStatus().equals(GeneralStatus.ACTIVE.getValue())
        ));
    }

    @Test
    void shouldThrowException_whenRoleNameAlreadyExists() {
        SaveRoleRequest request = SaveRoleRequest.builder()
                .name("admin")
                .description("admin role")
                .build();

        when(roleRepository.existsRoleByName("admin")).thenReturn(true);

        MainException exception = assertThrows(MainException.class, () -> {
            roleService.saveRole(request);
        });
        assertEquals("Role name already exists", exception.getMessage());
        verify(roleRepository).existsRoleByName("admin");
        verify(roleRepository, never()).save(any());
    }

    @Test
    void shouldUpdateRole_whenNameIsDifferentAndNotExists() {
        Long roleId = 1L;
        SaveRoleRequest request = SaveRoleRequest.builder()
                .name("new-admin")
                .description("updated desc")
                .build();

        Role existingRole = Role.builder()
                .id(roleId)
                .name("old-admin")
                .description("old desc")
                .status(GeneralStatus.ACTIVE.getValue())
                .build();

        when(roleRepository.findRoleByIdAndStatus(roleId, GeneralStatus.ACTIVE.getValue())).thenReturn(existingRole);
        when(roleRepository.existsRoleByName("new-admin")).thenReturn(false);

        roleService.updateRole(request, roleId);

        verify(roleRepository).existsRoleByName("new-admin");
        verify(roleRepository).save(argThat(role ->
                role.getName().equals("new-admin") &&
                        role.getDescription().equals("updated desc")));
    }

    @Test
    void shouldUpdateRole_whenNameIsSame_ignoreValidation() {
        Long roleId = 1L;
        SaveRoleRequest request = SaveRoleRequest.builder()
                .name("admin")
                .description("desc updated")
                .build();

        Role existingRole = Role.builder()
                .id(roleId)
                .name("admin")
                .description("desc old")
                .status(GeneralStatus.ACTIVE.getValue())
                .build();

        when(roleRepository.findRoleByIdAndStatus(roleId, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(existingRole);
        roleService.updateRole(request, roleId);

        verify(roleRepository, never()).existsRoleByName(any());
        verify(roleRepository).save(argThat(role ->
                role.getName().equals("admin") &&
                        role.getDescription().equals("desc updated")
        ));
    }

    @Test
    void shouldThrowException_whenRoleNotFound() {
        Long roleId = 100L;
        SaveRoleRequest request = SaveRoleRequest.builder()
                .name("new-role")
                .description("test")
                .build();

        when(roleRepository.findRoleByIdAndStatus(roleId, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(null);
        MainException exception = assertThrows(MainException.class, () -> {
            roleService.updateRole(request, roleId);
        });

        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenNewRoleNameAlreadyExists() {
        Long roleId = 1L;
        SaveRoleRequest request = SaveRoleRequest.builder()
                .name("existing-role")
                .description("desc")
                .build();

        Role existingRole = Role.builder()
                .id(roleId)
                .name("old-role")
                .description("desc old")
                .status(GeneralStatus.ACTIVE.getValue())
                .build();

        when(roleRepository.findRoleByIdAndStatus(roleId, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(existingRole);
        when(roleRepository.existsRoleByName("existing-role")).thenReturn(true);

        MainException exception = assertThrows(MainException.class, () -> {
            roleService.updateRole(request, roleId);
        });

        assertEquals("Role name already exists", exception.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void shouldSoftDeleteRole_whenRoleExists() {
        Long roleId = 1L;
        Role existingRole = Role.builder()
                .id(roleId)
                .name("admin")
                .status(GeneralStatus.ACTIVE.getValue())
                .build();

        when(roleRepository.findRoleByIdAndStatus(roleId, GeneralStatus.ACTIVE.getValue())).thenReturn(existingRole);
        roleService.deleteRole(roleId);

        verify(roleRepository).save(argThat(role ->
                role.getId().equals(roleId) &&
                        role.getStatus().equals(GeneralStatus.INACTIVE.getValue())));
    }

    @Test
    void shouldThrowException_whenRoleToDeleteNotFound() {
        Long roleId = 999L;
        when(roleRepository.findRoleByIdAndStatus(roleId, GeneralStatus.ACTIVE.getValue()))
                .thenReturn(null);

        MainException exception = assertThrows(MainException.class, () -> {
            roleService.deleteRole(roleId);
        });

        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository, never()).save(any());
    }
}
