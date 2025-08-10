package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.entity.Permission;
import com.ecommerce.auth_service.model.request.SavePermissionRequest;
import com.ecommerce.auth_service.model.response.BaseResponse;
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

    @GetMapping
    public ResponseEntity<BaseResponse> findPermissions() {
        List<Permission> permissions = permissionService.findPermissions();
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Permissions data", permissions, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getPermission(@RequestParam("id") Long id) {
        Permission permission = permissionService.findPermission(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Permission data", permission, null));
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> savePermission(@Valid @RequestBody SavePermissionRequest request) {
        permissionService.savePermission(request);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.CREATED.toString(), "Permission saved successfully", null, null));
    }

    @PostMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> updatePermission(@Valid @RequestBody SavePermissionRequest request, @PathVariable(value = "id") Long id) {
        permissionService.updatePermission(request, id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Permission updated successfully", null, null));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<BaseResponse> deletePermission(@PathVariable(value = "id") Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Permission deleted successfully", null, null));
    }
}
