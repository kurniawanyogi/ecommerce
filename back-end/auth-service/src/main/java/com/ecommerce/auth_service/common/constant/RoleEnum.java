package com.ecommerce.auth_service.common.constant;

public enum RoleEnum {
    SELLER("Seller"),
    ADMIN("Admin"),
    CUSTOMER("Customer");

    private final String name;
    RoleEnum(String name) {
        this.name = name;
    }
}
