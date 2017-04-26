package uk.gov.ons.ctp.response.casesvc.endpoint;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(CTPException.class)
    public ResponseEntity<?> handleCTPException(CTPException exception) {
        log.error("handleCTPException {}", exception);

        HttpStatus status;
        switch (exception.getFault()) {
            case RESOURCE_NOT_FOUND:
                status = HttpStatus.NOT_FOUND;
                break;
            case RESOURCE_VERSION_CONFLICT:
                status = HttpStatus.CONFLICT;
                break;
            case ACCESS_DENIED:
                status = HttpStatus.UNAUTHORIZED;
                break;
            case BAD_REQUEST:
            case VALIDATION_FAILED:
                status = HttpStatus.BAD_REQUEST;
                break;
            case SYSTEM_ERROR:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                log.error("Internal System Error", exception);
                break;
            default:
                status = HttpStatus.I_AM_A_TEAPOT;
                break;
        }

        return new ResponseEntity<>(exception, status);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleGeneralException(Throwable t) {
        log.error("handleGeneralException {}", t);
        return new ResponseEntity<>(new CTPException(CTPException.Fault.SYSTEM_ERROR, t, t.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<?> handleInvalidRequestException(InvalidRequestException ex, Locale locale) {
        log.error("handleInvalidRequestException {}", ex);
        StringBuilder logMsg = new StringBuilder(ex.getSourceMessage());

        StringBuilder responseMsg = new StringBuilder();
        List<FieldError> fieldErrors = ex.getErrors().getFieldErrors();
        for (Iterator<FieldError> errorsIte = fieldErrors.listIterator(); errorsIte.hasNext(); ) {
            FieldError fieldError = errorsIte.next();
            responseMsg.append(fieldError.getDefaultMessage());
            if (errorsIte.hasNext()) {
                responseMsg.append(",");
            }
        }

        log.error("logMsg is '{}' - responseMsg is '{}'", logMsg.toString(), responseMsg.toString());
        CTPException ourException = new CTPException(CTPException.Fault.BAD_REQUEST, responseMsg.toString());
        return new ResponseEntity<>(ourException, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, Locale locale) {
        log.error("handleHttpMessageNotReadableException {}", ex);
        CTPException ourException = new CTPException(CTPException.Fault.VALIDATION_FAILED, ex,
                ex.getCause().getMessage());
        return new ResponseEntity<>(ourException, HttpStatus.BAD_REQUEST);
    }
}
