package nl.debijenkorf.bsl.core;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class BSLResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private String defaultErrorMessage = "An error has occurred.";
    private HttpStatus defaultStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;

    @ExceptionHandler(BSLException.class)
    public ResponseEntity<Object> handleBSLException(BSLException ex, WebRequest request){
        String bodyOfResponse = ex.getResponseBody();
        HttpStatus status = ex.getStatusCode();

        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherException(Exception ex, WebRequest request){
        String bodyOfResponse = defaultErrorMessage;
        HttpStatus status = defaultStatusCode;

        ResponseStatus annotation = ex.getClass().getAnnotation(ResponseStatus.class);
        if(annotation != null)
        {
            bodyOfResponse = annotation.reason();
            status = annotation.value();
        }

        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), status, request);
    }

}
