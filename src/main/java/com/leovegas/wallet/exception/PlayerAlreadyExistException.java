package com.leovegas.wallet.exception;

public class PlayerAlreadyExistException extends RuntimeException {

    public PlayerAlreadyExistException() {
        super();
    }

    public PlayerAlreadyExistException(String message) {
        super(message);
    }

    public PlayerAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
