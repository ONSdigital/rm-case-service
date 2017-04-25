package uk.gov.ons.ctp.response.casesvc.error;

import org.springframework.validation.Errors;

public class InvalidRequestException extends RuntimeException {

    private Errors errors;
    private String sourceMessage = "Invalid Request ";

    public InvalidRequestException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }

    public String getSourceMessage() {
        return sourceMessage;
    }
}
