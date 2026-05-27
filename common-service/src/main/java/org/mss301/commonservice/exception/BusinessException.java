package org.mss301.commonservice.exception;

import lombok.Getter;
import org.mss301.commonservice.utils.MessagesUtils;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public BusinessException(String message, Object... args) {
        this(HttpStatus.BAD_REQUEST, MessagesUtils.getMessage(message, args));
    }

    public BusinessException(HttpStatus status, String message, Object... args) {
        super(MessagesUtils.getMessage(message, args));
        this.status = status;
        this.errorCode = status.name();
    }
}
