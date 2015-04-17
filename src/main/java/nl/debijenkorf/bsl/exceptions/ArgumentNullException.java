package nl.debijenkorf.bsl.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Missing parameter.")
public class ArgumentNullException extends RuntimeException {

    public  ArgumentNullException(String msg){
        super(msg);
    }
}
