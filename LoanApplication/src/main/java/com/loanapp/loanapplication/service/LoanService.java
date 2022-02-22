package com.loanapp.loanapplication.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.model.dto.LoanDto;

import java.util.List;
import java.util.Map;

public interface LoanService {

    Map<Double, Boolean> applyLoan(Long tckn);

    Map<Double, Boolean> loanApplicationProcessor(Integer creditScore, Double monthlySalary);

    List<LoanDto> getLoans(ObjectNode objectNode);

    List<LoanDto> getApprovedLoansById(Long tckn);

    List<LoanDto> getAllLoansById(Long tckn);
}
