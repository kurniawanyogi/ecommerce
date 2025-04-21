package com.ecommerce.user_management_service.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ROLE {
    CUSTOMER("customer"),
    SELLER("seller"),
    ADMIN("admin");

    private final String name;
}
