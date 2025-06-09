package com.ecommerce.auth_service.common.constant;

import lombok.Getter;

@Getter
public enum GeneralStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String value;

    GeneralStatus(String value) {
        this.value = value;
    }

    public static GeneralStatus fromString(String status) {
        if (status == null) return null;
        for (GeneralStatus gs : GeneralStatus.values()) {
            if (gs.getValue().equalsIgnoreCase(status.trim())) {
                return gs;
            }
        }
        return null;
    }
}
