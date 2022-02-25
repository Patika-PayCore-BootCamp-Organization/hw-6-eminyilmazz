package com.loanapp.loanapplication.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.exception.IllegalTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
import com.loanapp.loanapplication.exception.TcknValidator;
import com.loanapp.loanapplication.messaging.SmsProducer;
import com.loanapp.loanapplication.messaging.SmsService;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.Loan;
import com.loanapp.loanapplication.model.dto.CustomerSmsDto;
import com.loanapp.loanapplication.repository.LoanRepository;
import com.loanapp.loanapplication.service.CustomerService;
import com.loanapp.loanapplication.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.loanapp.loanapplication.model.dto.CustomerMapper.toSmsDto;
import static com.loanapp.loanapplication.service.impl.CreditScoreService.calculateCreditScore;

@Service
public class LoanServiceImpl implements LoanService {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private SmsProducer smsProducer;
    /**
     * This is a method to apply for loan for already existing customers.
     * This method was initially intented to expect a Customer and save the Customer if it does not exist already.
     * This idea was pushed to a later versions due to some complications. In the current version, only already existing customer can apply for loan.
     * @param tckn - For the customer who is applying for the loan.
     * @return Map<Double, Boolean> - The map is always singleton. The key value of it is the loan amount. It is 0 if it is denied.
     * Boolean value is the approval status of the application.
     * @throws NotFoundException - This exception is possibly originated from customerService.getByTckn(). It is re-thrown to be handled on the controller level.
     */
    @Override
    public Map<Double, Boolean> applyLoan(Long tckn) {
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
                smsProducer.messageOnLoanApproval(toSmsDto(customer, LocalDateTime.now(), loanApplicationResponse.keySet().iterator().next()));
            }
            return loanApplicationResponse;
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
    /**
     * This is a method is to determine the amount of loan.
     * @param creditScore - Integer, to be used to determine loan interval.
     * @param monthlySalary - Double, to be used to determine loan amount.
     * @return Map<Double, Boolean> - The map is always singleton. The key value of it is the loan amount. It is 0 if it is denied.
     * Boolean value is the approval status of the application.
     */
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
    /**
     * This is a method is to get loans of a customer. It calls one of the two methods; getApprovedLoansById or getAllLoansById.
     * This is decided by objectNode's "approved" field. It returns only approved loans or all.
     * @param objectNode fields: tckn - 11 digits number, (optional) approved - boolean.
     * @return List<LoanDto> - List of the loans the customer applied before. Only approved loans if "approved" of objectNode is true.
     * @throws IllegalArgumentException thrown if the objectNode does not have a "tckn" field.
     * @throws NotFoundException thrown if the customer is not found.
     * @see #getAllLoansById(Long) 
     * @see #getApprovedLoansById(Long)
     */
    @Override
    public List<Loan> getLoans(ObjectNode objectNode) {
        if (!objectNode.has("tckn")) {
            throw new IllegalArgumentException("Provided body is not valid.\nBody needs to have an 11 digits value for TCKN." +
                    "\nExample:\n{\n\"tckn\" : \"12345678910\",\n\"approved\" : true\"\n}");
        }
        try{
            TcknValidator.validate(objectNode.get("tckn").asLong());
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
    /**
     * This is a one of the branches of getLoans. It is used only to get approved loans.
     * Loans that are not approved for a customer are filtered out.
     * @param tckn
     * @return List<LoanDto> - List of the approved loans the customer applied before.
     * @throws NotFoundException - thrown if the tckn does not exist in the repository.
     * @see #getLoans(ObjectNode)
     * @see #getAllLoansById(Long) 
     */
    @Override
    public List<Loan> getApprovedLoansById(Long tckn) {
        if(!customerService.existById(tckn)) {
            throw new NotFoundException("Customer tckn: " + tckn + " not found!");
        } else {
            List<Loan> loanList = loanRepository.findAllByCustomer_tckn(tckn);
            return loanList.stream()
                    .filter(Loan::isApprovalStatus)
                    .collect(Collectors.toList());
        }
    }
    /**
     * This is another one of the branches of getLoans. It is used to get all loans.
     * Loans that are not approved for a customer are filtered out.
     * @param tckn
     * @return List<LoanDto> - List of the approved loans the customer applied before.
     * @throws NotFoundException - thrown if the tckn does not exist in the repository.
     * @see #getLoans(ObjectNode)
     * @see #getApprovedLoansById(Long)
     */
    @Override
    public List<Loan> getAllLoansById(Long tckn) {
        if(!customerService.existById(tckn)) {
            throw new NotFoundException("Customer tckn: " + tckn + " not found!");
    } else {
            return loanRepository.findAllByCustomer_tckn(tckn);
        }
    }
    /**
     * Delete method for loans of an existing Customer.
     * This method is called to delete a Customer and the Loans of Customer.
     * @param tckn of the customer to be deleted.
     * @return A generic ResponseEntity<> - Returns an OK status if deletion is successful.
     * @throws NotFoundException - if a Customer does not exist for the provided tckn.
     */
    @Override
    public boolean deleteAllByCustomer_tckn(Long tckn) {
        if (!customerService.existById(tckn)) {
            throw new NotFoundException("Delete operation is not successful. The customer does not exist.");
        }
        else {
            loanRepository.deleteAllByCustomer_Tckn(tckn);
            return true;
        }
    }
}
