package com.ecommerce.user_management_service.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class BaseResponse {
    private String code;
    private String description;
    private Map<String, Object> additionalEntity = new HashMap();

    public BaseResponse(String code, String description, Object additionalEntity) {
        HashMap data = new HashMap();
        data.put("data", additionalEntity);
        this.code = code;
        this.description = description;
        this.additionalEntity = data;
    }
}
