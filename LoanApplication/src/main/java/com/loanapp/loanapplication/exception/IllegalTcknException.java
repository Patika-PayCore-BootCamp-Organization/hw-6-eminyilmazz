package com.loanapp.loanapplication.exception;

public class IllegalTcknException extends IllegalArgumentException {
    public IllegalTcknException(String s) {
        super("Illegal TCKN Exception: " + s);
    }
}
