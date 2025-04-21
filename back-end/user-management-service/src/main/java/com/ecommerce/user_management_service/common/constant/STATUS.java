package com.ecommerce.user_management_service.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum STATUS {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DEACTIVATED("deactivated");

    private final String name;
}
