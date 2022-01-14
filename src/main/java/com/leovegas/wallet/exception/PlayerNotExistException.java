package com.leovegas.wallet.exception;

public class PlayerNotExistException extends RuntimeException {

    public PlayerNotExistException() {
        super();
    }

    public PlayerNotExistException(String message) {
        super(message);
    }

    public PlayerNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerNotExistException(Throwable cause) {
        super(cause);
    }
}
