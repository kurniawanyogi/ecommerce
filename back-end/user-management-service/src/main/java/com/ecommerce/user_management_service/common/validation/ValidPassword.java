package com.ecommerce.user_management_service.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Documented
public @interface ValidPassword {

    String message() default "Password must be at least 8 characters and contain upper, lower case letters and numbers";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

