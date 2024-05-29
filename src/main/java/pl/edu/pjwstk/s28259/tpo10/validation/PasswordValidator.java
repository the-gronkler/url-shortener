package pl.edu.pjwstk.s28259.tpo10.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private record RegexErrorPair(String regex, String errorMessage) { }

    public static int MIN_LENGTH = 10;
    public static int MIN_LOWERCASE = 1;
    public static int MIN_UPPERCASE = 2;
    public static int MIN_DIGITS = 3;
    public static int MIN_SPECIAL = 4;

    public static final String SPECIAL_REGEX = buildRegex("!@#\\$%\\^&*", MIN_SPECIAL);
    public static final String LOWERCASE_REGEX = buildRegex("[a-z]", MIN_LOWERCASE);
    public static final String UPPERCASE_REGEX = buildRegex("[A-Z]", MIN_UPPERCASE);
    public static final String DIGIT_REGEX = buildRegex("\\d", MIN_DIGITS);

    private static final List<RegexErrorPair> REGEX_ERROR_PAIRS = List.of(
            new RegexErrorPair(SPECIAL_REGEX, "Password must contain at least " + MIN_SPECIAL + " special character(s)"),
            new RegexErrorPair(LOWERCASE_REGEX, "Password must contain at least " + MIN_LOWERCASE + " lowercase character(s)"),
            new RegexErrorPair(UPPERCASE_REGEX, "Password must contain at least " + MIN_UPPERCASE + " uppercase character(s)"),
            new RegexErrorPair(DIGIT_REGEX, "Password must contain at least " + MIN_DIGITS + " digit(s)")
    );

    private static String buildRegex(String pattern, int minCount) {
        return "^(?=(" + pattern + ".*){" + minCount + ",}).*$";
    }

    @Override
    public void initialize(ValidPassword constraintAnnotation) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (password.length() < MIN_LENGTH) {
            context.buildConstraintViolationWithTemplate(
                    "Password must be at least " + MIN_LENGTH + " characters long"
            ).addConstraintViolation();
            return false;
        }

        for (RegexErrorPair pair : REGEX_ERROR_PAIRS) {
            if (!password.matches(pair.regex())) {
                context.buildConstraintViolationWithTemplate(pair.errorMessage())
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
