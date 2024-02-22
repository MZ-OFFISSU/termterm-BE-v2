package site.termterm.api.global.handler.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.termterm.api.global.exception.ResponseDto;

@RestControllerAdvice
public class CustomExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(CustomApiException.class)     // CustomApiException 이 발생하면 이 메소드가 호출된다.
    public ResponseEntity<?> apiException(CustomApiException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1, e.getMessage(), e.getData()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> validationApiException(CustomValidationException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1, e.getMessage(), e.getErrorMap()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomForbiddenException.class)
    public ResponseEntity<?> forbiddenException(CustomForbiddenException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1, e.getMessage(), null), HttpStatus.FORBIDDEN);
    }
}
