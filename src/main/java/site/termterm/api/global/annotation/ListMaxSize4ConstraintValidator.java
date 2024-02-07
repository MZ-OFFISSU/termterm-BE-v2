package site.termterm.api.global.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ListMaxSize4ConstraintValidator implements ConstraintValidator<ListMaxSize4Constraint, List<String>> {
    @Override
    public boolean isValid(List<String> list, ConstraintValidatorContext context){
        int size = list.size();
        return 1 <= size && size <= 4;
    }
}
