package com.ecommerce.auth_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRedis implements Serializable {
    private Long id;
    private String email;
    private String fullName;
    private List<String> roles;
    private List<String> permissions;
}
