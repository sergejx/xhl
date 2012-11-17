package sk.tuke.xhl.core.validator;

import java.util.List;

import sk.tuke.xhl.core.Error;

public class ValidationException extends RuntimeException {
    private final List<sk.tuke.xhl.core.Error> errors;

    public ValidationException(List<Error> errors) {
        this.errors = errors;
    }

    @Override
    public String getMessage() {
        String message = "Validation Errors: \n";
        for (Error  error: errors) {
            message += error.position + " " + error.message + "\n";
        }
        return message;
    }
}
