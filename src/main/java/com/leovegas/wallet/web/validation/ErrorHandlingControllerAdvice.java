package com.leovegas.wallet.web.validation;

import com.leovegas.wallet.exception.JoinEntitiesFailedException;
import com.leovegas.wallet.exception.PlayerAlreadyExistException;
import com.leovegas.wallet.exception.PlayerNotExistException;
import com.leovegas.wallet.exception.TransactionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(ErrorHandlingControllerAdvice.class);


    @ExceptionHandler(PlayerAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    String onPlayerAlreadyExistException(PlayerAlreadyExistException e) {
        return e.getMessage();
    }

    @ExceptionHandler(TransactionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onTransactionFailedException(TransactionFailedException e) {
        log.warn("Transaction failed.", e);
        return e.getMessage();
    }

    @ExceptionHandler(PlayerNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onPlayerNotExistException(PlayerNotExistException e) {
        log.info("Could not find a player with the provided id.", e);
        return e.getMessage();
    }

    @ExceptionHandler(JoinEntitiesFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    String onJoinEntitiesFailedException(JoinEntitiesFailedException e) {
        log.error("Could not join/connect two critical entities together.", e);
        return e.getMessage();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstrainViolationException(ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        e.getConstraintViolations().forEach(violation -> error.getViolations().add(
                new Violation(violation.getPropertyPath().toString(), violation.getMessage())
        ));
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Field constraints breached.", e);
        ValidationErrorResponse error = new ValidationErrorResponse();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> error.getViolations().add(
                new Violation(fieldError.getField(), fieldError.getDefaultMessage())));
        return error;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return e.getMessage();
    }

}
