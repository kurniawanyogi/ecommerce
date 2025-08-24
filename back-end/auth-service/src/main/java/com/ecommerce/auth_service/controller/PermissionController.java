package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.entity.Permission;
import com.ecommerce.auth_service.model.request.SavePermissionRequest;
import com.ecommerce.auth_service.model.response.BaseResponse;
import com.ecommerce.auth_service.security.RequiredPermission;
import com.ecommerce.auth_service.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @RequiredPermission("permission:read")
    @GetMapping
    public ResponseEntity<BaseResponse> findPermissions() {
        List<Permission> permissions = permissionService.findPermissions();
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Permissions data", permissions, null));
    }

    @RequiredPermission("permission:read")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getPermission(@RequestParam("id") Long id) {
        Permission permission = permissionService.findPermission(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Permission data", permission, null));
    }

    @RequiredPermission("permission:write")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> savePermission(@Valid @RequestBody SavePermissionRequest request) {
        permissionService.savePermission(request);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.CREATED.toString(), "Permission saved successfully", null, null));
    }

    @RequiredPermission("permission:write")
    @PostMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> updatePermission(@Valid @RequestBody SavePermissionRequest request, @PathVariable(value = "id") Long id) {
        permissionService.updatePermission(request, id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Permission updated successfully", null, null));
    }

    @RequiredPermission("permission:delete")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<BaseResponse> deletePermission(@PathVariable(value = "id") Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Permission deleted successfully", null, null));
    }
}
