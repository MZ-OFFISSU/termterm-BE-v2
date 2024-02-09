package site.termterm.api.global.handler.exceptions;

import lombok.Getter;

@Getter
public class CustomApiException extends RuntimeException{
    private Object data;

    public CustomApiException(String message) {
        super(message);
    }

    public CustomApiException(String message, Object data) {
        super(message);
        this.data = data;
    }
}
