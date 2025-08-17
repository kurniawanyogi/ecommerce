package com.ecommerce.auth_service.common.constant;

public enum RoleEnum {
    USER("User"),
    SELLER("Seller"),
    ADMIN("Admin"),
    CUSTOMER("Customer");

    private final String name;
    RoleEnum(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
