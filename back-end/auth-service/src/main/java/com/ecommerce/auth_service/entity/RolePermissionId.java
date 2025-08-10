package com.ecommerce.auth_service.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RolePermissionId implements Serializable {
    private Long roleId;
    private Long permissionId;
}
