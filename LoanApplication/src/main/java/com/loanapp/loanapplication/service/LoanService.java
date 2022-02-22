package com.loanapp.loanapplication.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.model.dto.LoanDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface LoanService {

    ResponseEntity<Map<Double, Boolean>> applyLoan(Long tckn);

    Map<Double, Boolean> loanApplicationProcessor(Integer creditScore, Double monthlySalary);

    List<LoanDto> getLoans(ObjectNode objectNode);

    List<LoanDto> getApprovedLoansById(Long tckn);

    List<LoanDto> getAllLoansById(Long tckn);
}
