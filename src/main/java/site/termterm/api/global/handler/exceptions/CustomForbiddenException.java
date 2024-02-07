package site.termterm.api.global.handler.exceptions;

import lombok.Getter;

@Getter
public class CustomForbiddenException extends RuntimeException{
    public CustomForbiddenException(String message) {
        super(message);
    }
}
