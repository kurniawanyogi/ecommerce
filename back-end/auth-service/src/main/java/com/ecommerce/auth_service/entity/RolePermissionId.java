package com.ecommerce.auth_service.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class RolePermissionId implements Serializable {
    private Long roleId;
    private Long permissionId;
}
