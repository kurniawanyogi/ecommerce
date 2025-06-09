package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.model.request.SaveRoleRequest;
import com.ecommerce.auth_service.model.response.BaseResponse;
import com.ecommerce.auth_service.service.RoleService;
import com.ecommerce.auth_service.service.impl.RoleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> saveRole(@Valid @RequestBody SaveRoleRequest request) {
        try {
            roleService.saveRole(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Role saved successfully", null, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null, e));
        }
    }
}
