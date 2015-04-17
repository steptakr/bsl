package nl.debijenkorf.bsl.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.util.StringUtils.isEmpty;

public abstract class BSLException extends RuntimeException {

    private HttpStatus status;
    private String body;

    protected BSLException(){ super(); }

    protected BSLException(String message){
        super(message);
    }

    protected BSLException(String message, Throwable cause){
        super(message, cause);
    }

    private ResponseStatus getResponseStatusAnnotation(){
        ResponseStatus annotation = this.getClass().getAnnotation(ResponseStatus.class);
        return annotation;
    }

    protected HttpStatus getStatusCode(){
        if(status != null) return status;

        ResponseStatus annotation = getResponseStatusAnnotation();
        if(annotation != null && annotation.value() != null)
            status = annotation.value();
        else
            status = HttpStatus.INTERNAL_SERVER_ERROR;

        return status;
    }

    protected String getResponseBody() {
        if(!isEmpty(body)) return body;

        ResponseStatus annotation = getResponseStatusAnnotation();
        if(annotation != null && !isEmpty(annotation.reason()))
            body = annotation.reason();
        else
            body = isEmpty(getMessage()) ? "An error has occurred." : getMessage();

        return body;
    }
}
