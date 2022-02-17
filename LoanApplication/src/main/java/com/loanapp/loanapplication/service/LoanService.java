package com.loanapp.loanapplication.service;

import java.util.Map;

public interface LoanService {

    Map<Double, Boolean> applyLoan(Long tckn);

    Map<Double, Boolean> loanApplicationProcessor(Integer creditScore, Double monthlySalary);
}
