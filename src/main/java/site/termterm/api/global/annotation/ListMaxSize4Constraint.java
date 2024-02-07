package site.termterm.api.global.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = ListMaxSize4ConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListMaxSize4Constraint {
    String message() default "The List<String> can contain at least 1 and up to 4 elements.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
