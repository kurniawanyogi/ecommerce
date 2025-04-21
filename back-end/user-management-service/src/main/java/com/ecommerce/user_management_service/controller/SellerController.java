package com.ecommerce.user_management_service.controller;

import com.ecommerce.user_management_service.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for seller operations
 * Handles: Update profile seller, verify seller, registration (could also be in AuthController)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
public class SellerController {
    private final SellerService sellerService;
}
