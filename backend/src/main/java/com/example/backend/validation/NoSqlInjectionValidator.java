package com.example.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class NoSqlInjectionValidator implements ConstraintValidator<NoSqlInjection, String> {

    private static final String SQL_PATTERN = "(?i).*\\b(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER|TRUNCATE|UNION|REVOKE|FROM|WHERE|TABLE|DATABASE)\\b.*|.*(--|;).*";

    private static final Pattern pattern = Pattern.compile(SQL_PATTERN);

    @Override
    public void initialize(NoSqlInjection constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Use @NotNull to validate nulls
        }
        return !pattern.matcher(value).find();
    }
}
