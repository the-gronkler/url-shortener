package pl.edu.pjwstk.s28259.tpo10.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        var errors = StaticPasswordValidator.getErrors(password);

        if (errors.isEmpty())
            return true;

        String allErrors = String.join(";", errors);
        context.buildConstraintViolationWithTemplate(allErrors)
                .addConstraintViolation();

        return false;
    }

    @Override
    public void initialize(ValidPassword constraintAnnotation) {}
}
