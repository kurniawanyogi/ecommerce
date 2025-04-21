package com.ecommerce.user_management_service.controller;

import com.ecommerce.user_management_service.model.request.CustomerRegistrationRequest;
import com.ecommerce.user_management_service.model.response.BaseResponse;
import com.ecommerce.user_management_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Controller for customer operations
 * Handles: Update profile customer, get profile
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping(path = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest payload) {
        customerService.registerCustomer(payload);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.CREATED.toString(), "Customer registration success", null));
    }
}
