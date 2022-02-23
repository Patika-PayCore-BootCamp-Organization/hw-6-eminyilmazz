package com.loanapp.loanapplication.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.exception.TcknValidator;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.Loan;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import com.loanapp.loanapplication.model.dto.LoanDto;
import com.loanapp.loanapplication.model.dto.LoanMapper;
import com.loanapp.loanapplication.service.CustomerService;
import com.loanapp.loanapplication.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Validated
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoanService loanService;
    /**
     * @apiNote GetMapping for all customers.
     * @return Iterable<Customer>
     */
    @GetMapping("/all")
    public Iterable<Customer> getAll(){
        Iterable<Customer> customers = customerService.getAll();
        List<Loan> loanList;
        for (Customer customer : customers) {
            loanList = loanService.getApprovedLoansById(customer.getTckn())
                    .stream()
                    .map(LoanMapper::toSimpleEntity)
                    .collect(Collectors.toList());
            customer.setLoanList(loanList);
        }
        return customers;
    }
    /**
     * @apiNote GetMapping for Customer.
     * @param tckn As a request parameter.
     * @return ResponseEntity<Customer>
     */
    @GetMapping
    public ResponseEntity<Customer> getByTckn(@RequestParam Long tckn){
        TcknValidator.validate(tckn);
        List<Loan> loanList = loanService.getApprovedLoansById(tckn)
                .stream()
                .map(LoanMapper::toSimpleEntity)
                .collect(Collectors.toList());
        Customer customer = customerService.getByTckn(tckn);
        customer.setLoanList(loanList);
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }
    /**
     * @apiNote Adds a Customer to database. Customer TCKN and phone number are validated.
     * @param customerDto as a request body.
     * @return A generic ResponseEntity<> - If adding customer is successful, status is OK, else BAD_REQUEST.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
        TcknValidator.validate(customerDto.getTckn());
        return customerService.addCustomer(customerDto);
    }
    /**
     * @apiNote Updates an already existing Customer. Customer TCKN and phone number are validated.
     * @param customerDto as a request body.
     * @return ResponseEntity<Customer> - If updating the customer is successful, status is OK, else NOT_FOUND.
     */
    @PutMapping("/update")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody CustomerDto customerDto) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(customerDto));
    }
    /**
     * @apiNote DeleteMapping for an existing Customer.
     * @param tckn as a request parameter.
     * @return A generic ResponseEntity<> - If to be deleted customer exists, returns an OK status. Else NOT_FOUND.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCustomer(@RequestParam(name = "tckn") Long tckn) {
        return customerService.deleteCustomer(tckn);
    }
    /**
     * @apiNote This API is to apply for loan. If the provided Customer does not in the customer table, it is saved. Else
     * does not update it and throws IllegalArgumentException.
     * @param tckn as a request parameter. Value is validated to be 11 digits number.
     * @return ResponseEntity<Map<Double, Boolean>> - Keys of the returned map is the loan amount. It is equal to 0D if it is declined.
     * Values of the returned map is th Boolean value of the approval status of the application.
     */
    @GetMapping ("/loan/apply")
    public ResponseEntity<Map<Double, Boolean>> applyLoan(@RequestParam(name = "tckn") Long tckn) {
        TcknValidator.validate(tckn);
        return ResponseEntity.ok(loanService.applyLoan(tckn));
    }
    /**
     * @apiNote This API is to get the history of loan applications of a customer.
     * @param objectNode fields: tckn, approved (optional)
     * @return if approved field in the objectNode is true, returns only approved loans as a LoanDto list. Else, all of them.
     */
    @GetMapping("/loan/history")
    public List<LoanDto> getLoans (@RequestBody ObjectNode objectNode) {
        return loanService.getLoans(objectNode);
    }
}
