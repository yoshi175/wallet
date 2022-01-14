package com.leovegas.wallet.exception;

public class InternalLogicException extends RuntimeException {

    public InternalLogicException() {
        super();
    }

    public InternalLogicException(String message) {
        super(message);
    }

    public InternalLogicException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalLogicException(Throwable cause) {
        super(cause);
    }
}
