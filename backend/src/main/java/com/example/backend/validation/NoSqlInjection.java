package com.example.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoSqlInjectionValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSqlInjection {
    String message() default "El campo contiene palabras reservadas de SQL o caracteres no permitidos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
