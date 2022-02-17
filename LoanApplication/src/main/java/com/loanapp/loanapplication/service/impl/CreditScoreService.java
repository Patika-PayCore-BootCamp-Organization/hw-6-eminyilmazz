package com.loanapp.loanapplication.service.impl;

public class CreditScoreService {

    /**
     * This static method only simulates a credit score service that is assumed to already exist.
     * Returns the last three digits of TCKN as an integer.
     * If the last three digits of TCKN is greater than 900, returns 1000.
     *
     * @param tckn 11 digit ID of Customer.class.
     * @return An Integer valuing between 0 and 1000.
    */
    public static Integer calculateCreditScore(Long tckn) {
        Integer creditScore = Math.toIntExact(tckn % 1000);
        return creditScore > 900 ? 1000 : creditScore;
    }
}