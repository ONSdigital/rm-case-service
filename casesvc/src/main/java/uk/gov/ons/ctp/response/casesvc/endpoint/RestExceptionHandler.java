package uk.gov.ons.ctp.response.casesvc.endpoint;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.ons.ctp.common.error.CTPException;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(CTPException.class)
    public ResponseEntity<?> handleError(CTPException exception) {
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

}
