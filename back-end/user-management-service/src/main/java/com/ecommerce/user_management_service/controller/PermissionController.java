package com.ecommerce.user_management_service.controller;

import com.ecommerce.user_management_service.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for permission operations
 * Handles: CRUD permission, assign to role (optional / internal only)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService permissionService;
}
