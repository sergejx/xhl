package xhl.core.validator;

import java.util.List;

import xhl.core.Error;

public class ValidationException extends RuntimeException {
    private final List<xhl.core.Error> errors;

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
