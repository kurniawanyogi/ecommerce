package com.ecommerce.user_management_service.service;

import com.ecommerce.user_management_service.model.request.CustomerRegistrationRequest;

/**
 * Service for customer operations
 * Handles: Update profile customer, get profile
 */
public interface CustomerService {
    void registerCustomer(CustomerRegistrationRequest payload);
}