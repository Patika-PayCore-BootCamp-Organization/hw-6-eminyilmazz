package com.loanapp.loanapplication.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.model.Loan;

import java.util.List;
import java.util.Map;

public interface LoanService {

    Map<Double, Boolean> applyLoan(Long tckn);

    Map<Double, Boolean> loanApplicationProcessor(Integer creditScore, Double monthlySalary);

    List<Loan> getLoans(ObjectNode objectNode);

    List<Loan> getApprovedLoansById(Long tckn);

    List<Loan> getAllLoansById(Long tckn);

    boolean deleteAllByCustomer_tckn(Long tckn);
}
