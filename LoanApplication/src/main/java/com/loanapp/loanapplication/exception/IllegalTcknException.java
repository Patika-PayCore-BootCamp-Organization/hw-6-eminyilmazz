package com.loanapp.loanapplication.exception;

public class IllegalTcknException extends IllegalArgumentException {
    public IllegalTcknException(String s) {
        super("Illegal TCKN Exception: " + s + "\nTCKN must be 11 digits unique number that can only contain numbers.");
    }
}
