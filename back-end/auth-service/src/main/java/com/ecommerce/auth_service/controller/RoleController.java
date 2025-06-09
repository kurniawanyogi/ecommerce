package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.model.request.SaveRoleRequest;
import com.ecommerce.auth_service.model.response.BaseResponse;
import com.ecommerce.auth_service.service.RoleService;
import com.ecommerce.auth_service.service.impl.RoleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> saveRole(@Valid @RequestBody SaveRoleRequest request) {
            roleService.saveRole(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Role saved successfully", null, null));
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
}