package com.loanapp.loanapplication.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.exception.IllegalTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
import com.loanapp.loanapplication.exception.TcknValidator;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.Loan;
import com.loanapp.loanapplication.model.dto.LoanDto;
import com.loanapp.loanapplication.model.dto.LoanMapper;
import com.loanapp.loanapplication.repository.LoanRepository;
import com.loanapp.loanapplication.service.CustomerService;
import com.loanapp.loanapplication.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.loanapp.loanapplication.service.impl.CreditScoreService.calculateCreditScore;

@Service
public class LoanServiceImpl implements LoanService {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LoanRepository loanRepository;

    @Override
    public ResponseEntity<Map<Double, Boolean>> applyLoan(Long tckn) {
        try {
            Customer customer = customerService.getByTckn(tckn);
            customer.setCreditScore(calculateCreditScore(tckn));
            Map<Double, Boolean> loanApplicationResponse = loanApplicationProcessor(customer.getCreditScore(),
                                                                                    customer.getMonthlySalary());
            if(loanApplicationResponse.containsValue(true)) {
                loanRepository.save(Loan.builder()
                                        .loanAmount((Double) loanApplicationResponse.keySet().toArray()[0])
                                        .approvalStatus(true)
                                        .customer(customer)
                                        .build());
            }
            return ResponseEntity.status(HttpStatus.OK).body(loanApplicationResponse);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public Map<Double, Boolean> loanApplicationProcessor(Integer creditScore, Double monthlySalary) {
        final Double CREDIT_LIMIT_MULTIPLIER = 4D;
        Double loanAmount = 0D;
        if(creditScore <= 500) {
            return Collections.singletonMap(loanAmount, false);
        } else if(creditScore != 1000) {
            loanAmount = monthlySalary < 5000 ? 10000D : 20000D;
            return Collections.singletonMap(loanAmount, true);
        } else {
            return Collections.singletonMap(CREDIT_LIMIT_MULTIPLIER * monthlySalary, true);
        }
    }

    @Override
    public List<LoanDto> getLoans(ObjectNode objectNode) {
        if (!objectNode.has("tckn")) {
            throw new IllegalArgumentException("Provided body is not valid.\nBody needs to have an 11 digits value for TCKN." +
                    "\nExample:\n{\n\"tckn\" : \"12345678910\",\n\"approved\" : true\"\n}");
        }
        try{
            TcknValidator.validateTckn(objectNode.get("tckn").asLong());
            boolean hasApproved = objectNode.has("approved");
            if (hasApproved && objectNode.get("approved").asBoolean()) {
                return getApprovedLoansById(objectNode.get("tckn").asLong());
            } else {
                return getAllLoansById(objectNode.get("tckn").asLong());
            }
        } catch (IllegalTcknException e) {
            throw new IllegalTcknException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public List<LoanDto> getApprovedLoansById(Long tckn) {
        if(!customerService.existById(tckn)) {
            throw new NotFoundException("Customer tckn: " + tckn + " not found!");
        } else {
            List<Loan> loanList = loanRepository.findAllByCustomer_tckn(tckn);
            return loanList.stream()
                    .filter(Loan::isApprovalStatus)
                    .map(LoanMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<LoanDto> getAllLoansById(Long tckn) {
        if(!customerService.existById(tckn)) {
            throw new NotFoundException("Customer tckn: " + tckn + " not found!");
    } else {
            List<Loan> loanList = loanRepository.findAllByCustomer_tckn(tckn);
            return loanList.stream()
                    .map(LoanMapper::toDto)
                    .collect(Collectors.toList());
        }
    }
}
