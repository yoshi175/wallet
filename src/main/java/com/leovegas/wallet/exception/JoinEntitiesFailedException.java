package com.leovegas.wallet.exception;

public class JoinEntitiesFailedException extends RuntimeException {

    public JoinEntitiesFailedException() {
        super();
    }

    public JoinEntitiesFailedException(String message) {
        super(message);
    }

    public JoinEntitiesFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public JoinEntitiesFailedException(Throwable cause) {
        super(cause);
    }
}
