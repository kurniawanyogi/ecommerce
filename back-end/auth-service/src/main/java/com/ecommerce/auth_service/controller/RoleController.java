package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.model.request.AssignPermissionRequest;
import com.ecommerce.auth_service.model.request.SaveRoleRequest;
import com.ecommerce.auth_service.model.response.BaseResponse;
import com.ecommerce.auth_service.service.RolePermissionService;
import com.ecommerce.auth_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;

    @GetMapping
    public ResponseEntity<BaseResponse> getRoles() {
        List<Role> roles = roleService.findRoles();
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Roles data", roles, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getRoles(@RequestParam("id") Long id) {
        Role role = roleService.findRole(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Role data", role, null));
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> saveRole(@Valid @RequestBody SaveRoleRequest request) {
            roleService.saveRole(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.CREATED.toString(), "Role saved successfully", null, null));
    }

    @PostMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> updateRole(@Valid @RequestBody SaveRoleRequest request, @PathVariable(value = "id") Long id) {
        roleService.updateRole(request, id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Role updated successfully", null, null));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<BaseResponse> deleteRole(@PathVariable(value = "id") Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Role deleted successfully", null, null));
    }

    @PostMapping("/assign-permissions")
    public ResponseEntity<String> assignPermissions(@RequestBody AssignPermissionRequest request) {
        rolePermissionService.assignPermissions(request.getRoleId(), request.getPermissionIdList());
        return ResponseEntity.ok("Permissions assigned to role");
    }
}