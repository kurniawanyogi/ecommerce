package com.ecommerce.auth_service.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignPermissionRequest {
    @NotNull
    private Long roleId;
    @NotNull
    private List<Long> permissionIdList;
}
