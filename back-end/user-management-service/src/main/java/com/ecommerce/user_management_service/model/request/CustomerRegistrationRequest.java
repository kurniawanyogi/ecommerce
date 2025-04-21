package com.ecommerce.user_management_service.model.request;

import com.ecommerce.user_management_service.common.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistrationRequest {

    @NotEmpty
    private String fullName;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    @ValidPassword
    private String password;

    @NotEmpty
    private String address;
}

