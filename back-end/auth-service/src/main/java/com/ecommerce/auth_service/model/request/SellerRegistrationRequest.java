package com.ecommerce.auth_service.model.request;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class SellerRegistrationRequest extends RegistrationUserRequest {
    @NotBlank(message = "ID number is required")
    @Size(max = 100, message = "ID number must be less than 100 characters")
    private String idNumber;

    @NotBlank(message = "ID type is required")
    @Size(max = 50, message = "ID type must be less than 50 characters")
    private String idType;
}
