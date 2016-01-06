package org.webbuilder.web.core.exception;

/**
 * Created by 浩 on 2015-12-23 0023.
 */
public class AccessValidException extends BusinessException {
    public AccessValidException(String message) {
        super(message);
    }

    public AccessValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
