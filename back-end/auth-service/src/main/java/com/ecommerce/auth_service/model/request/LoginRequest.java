package com.ecommerce.auth_service.model.request;

import com.ecommerce.auth_service.validator.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotEmpty
    private String email;
    @PasswordValidator
    private String password;
}
