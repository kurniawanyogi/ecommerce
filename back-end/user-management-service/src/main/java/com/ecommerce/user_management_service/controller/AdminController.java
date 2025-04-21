package com.ecommerce.user_management_service.controller;

import com.ecommerce.user_management_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for admin operations related to users
 * Handles: List customers, list sellers, deactivate user (fraud handling)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminController {
    private final AdminService adminService;
}
