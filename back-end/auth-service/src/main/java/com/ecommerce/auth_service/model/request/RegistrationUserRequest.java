package com.ecommerce.auth_service.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationUserRequest {
    @Email
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
    @NotBlank(message = "password is required")
    private String confirmPassword;
    @Size(max = 50, message = "password must be less than 50 characters")
    private String phoneNumber;
    @NotBlank(message = "first name is required")
    @Size(max = 100, message = "first name must be less than 100 characters")
    private String firstName;
    @Size(max = 100, message = "last name must be less than 100 characters")
    private String lastName;
    private String birthDate;
    @Size(max = 20, message = "gender must be less than 20 characters")
    private String gender;
    @Size(max = 100, message = "id number must be less than 100 characters")
    private String idNumber;
    @Size(max = 50, message = "id type must be less than 50 characters")
    private String idType;
}
