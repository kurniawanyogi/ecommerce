package com.ecommerce.auth_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginCache {
    private String token;
    private UserRedis userRedis;
}
