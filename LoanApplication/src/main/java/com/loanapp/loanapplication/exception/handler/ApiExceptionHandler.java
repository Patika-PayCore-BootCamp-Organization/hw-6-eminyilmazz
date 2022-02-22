package com.loanapp.loanapplication.exception.handler;

import com.loanapp.loanapplication.exception.DuplicateTcknException;
import com.loanapp.loanapplication.exception.IllegalTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler ({NotFoundException.class})
    public ResponseEntity<String> notFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({DuplicateTcknException.class, IllegalTcknException.class})
    public ResponseEntity<String> tcknException(DuplicateTcknException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
