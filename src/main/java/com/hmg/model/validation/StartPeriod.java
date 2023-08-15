package com.hmg.model.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = StartPeriodValidator.class)
public @interface StartPeriod {

    String message() default "{not_future_date_or_before_2018}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
