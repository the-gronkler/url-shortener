package pl.edu.pjwstk.s28259.tpo10.validation;

import java.util.ArrayList;
import java.util.List;

// separate static pojo class to handle finding invalid passwords
// I separated this logic form the ConstraintValidator to make testing easier,
// since writing regex is very error-prone.

public class StaticPasswordValidator {
    private record RegexErrorPair(String regex, String errorMessage) { }

    public static final int MIN_LENGTH = 10;
    public static final int MIN_LOWERCASE = 1;
    public static final int MIN_UPPERCASE = 2;
    public static final int MIN_DIGITS = 3;
    public static final int MIN_SPECIAL = 4;

    public static final String SPECIAL_CHARS = "!@#$%^&*";

    public static final String SPECIAL_REGEX = buildRegex("[" + SPECIAL_CHARS + "]", MIN_SPECIAL);
    public static final String LOWERCASE_REGEX = buildRegex("[a-z]", MIN_LOWERCASE);
    public static final String UPPERCASE_REGEX = buildRegex("[A-Z]", MIN_UPPERCASE);
    public static final String DIGIT_REGEX = buildRegex("\\d", MIN_DIGITS);

    private static final String LENGTH_ERROR_MESSAGE = "Password must be at least " + MIN_LENGTH + " characters long";

    private static final List<RegexErrorPair> REGEX_ERROR_PAIRS = List.of(
            new RegexErrorPair(SPECIAL_REGEX, "Password must contain at least " + MIN_SPECIAL + " special character(s)"),
            new RegexErrorPair(LOWERCASE_REGEX, "Password must contain at least " + MIN_LOWERCASE + " lowercase character(s)"),
            new RegexErrorPair(UPPERCASE_REGEX, "Password must contain at least " + MIN_UPPERCASE + " uppercase character(s)"),
            new RegexErrorPair(DIGIT_REGEX, "Password must contain at least " + MIN_DIGITS + " digit(s)")
    );

    private static String buildRegex(String pattern, int minCount) {
        return "^(.*" + pattern + "){" + minCount + ",}.*$";
    }

    public static List<String> getErrors(String password) {

        List<String> errors = new ArrayList<>();
        if (password == null || password.isEmpty()) {
            return errors;
        }

        if (password.length() < MIN_LENGTH)
            errors.add(LENGTH_ERROR_MESSAGE);


        for (RegexErrorPair pair : REGEX_ERROR_PAIRS)
            if (!password.matches(pair.regex()))
                errors.add(pair.errorMessage());

        return errors;
    }

}
