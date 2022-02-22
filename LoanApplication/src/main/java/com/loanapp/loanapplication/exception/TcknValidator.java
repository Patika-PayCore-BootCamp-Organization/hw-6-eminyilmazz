package com.loanapp.loanapplication.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TcknValidator {

    private static final String _tcknRegex = "^[0-9]{11}";

    public static boolean validateTckn(Long tckn){
        String tcknString = String.valueOf(tckn);
        Pattern pattern = Pattern.compile(_tcknRegex);
        Matcher matcher = pattern.matcher(tcknString);
        if(!matcher.matches()){
            throw new IllegalTcknException("TCKN needs to be 11 digits and can only contain only numbers.");
        } else {
            return true;
        }
    }
}