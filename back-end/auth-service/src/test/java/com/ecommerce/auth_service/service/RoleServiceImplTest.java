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
                        role.getStatus().equals("ACTIVE")
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
}
