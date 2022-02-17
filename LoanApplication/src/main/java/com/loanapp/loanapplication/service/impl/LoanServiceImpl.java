package com.loanapp.loanapplication.service.impl;

import com.loanapp.loanapplication.exception.IllegalTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.Loan;
import com.loanapp.loanapplication.repository.LoanRepository;
import com.loanapp.loanapplication.service.CustomerService;
import com.loanapp.loanapplication.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.loanapp.loanapplication.service.impl.CreditScoreService.calculateCreditScore;

@Service
public class LoanServiceImpl implements LoanService {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LoanRepository loanRepository;

    @Override
    public Map<Double, Boolean> applyLoan(Long tckn) {
        if (tckn.toString().length() != 11) {
        throw new IllegalTcknException("TCKN must be an 11 digits number!");
        }
        try {
            Customer customer = customerService.findById(tckn);
            customer.setCreditScore(calculateCreditScore(tckn));
            Map<Double, Boolean> loanApplicationResponse = loanApplicationProcessor(customer.getCreditScore(),
                                                                                    customer.getMonthlySalary());
            if(loanApplicationResponse.containsValue(true)) {
                loanRepository.save(new Loan((Double) loanApplicationResponse.keySet().toArray()[0], customer));
            }
            return loanApplicationResponse;
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
}
