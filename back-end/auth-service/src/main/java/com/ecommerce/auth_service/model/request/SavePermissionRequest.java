package com.ecommerce.auth_service.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavePermissionRequest {
    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be less than 100 characters")
    private String name;

    @NotBlank(message = "description is required")
    @Size(max = 255, message = "description must be less than 255 characters")
    private String description;
}
