package site.termterm.api.global.handler.exceptions;

import lombok.Getter;

@Getter
public class CustomStatusApiException extends RuntimeException{
    private Integer status;
    private Object data;

    public CustomStatusApiException(Integer status, String message) {
        super(message);
        this.status = status;
    }

    public CustomStatusApiException(Integer status, String message, Object data) {
        super(message);
        this.status = status;
        this.data = data;
    }
}