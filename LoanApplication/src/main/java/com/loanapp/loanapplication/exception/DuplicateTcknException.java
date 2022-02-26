package com.loanapp.loanapplication.exception;

public class DuplicateTcknException extends RuntimeException{

    private static final String genericMessage = "Provided TCKN already exists.\nCannot accept duplicate TCKN.\n";
    public DuplicateTcknException() {
        super(genericMessage);
    }
}
