package com.loanapp.loanapplication.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.exception.DuplicateTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
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

import static com.loanapp.loanapplication.model.dto.CustomerMapper.toDto;

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
       try {
           return customerService.addCustomer(customerDto);
        } catch (DuplicateTcknException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
       }
    }
    /**
     * @apiNote Updates an already existing Customer. Customer TCKN and phone number are validated.
     * @param customerDto as a request body.
     * @return ResponseEntity<Customer> - If updating the customer is successful, status is OK, else NOT_FOUND.
     */
    @PutMapping("/update")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody CustomerDto customerDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(customerDto));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
     * @param customerDto as a request body.
     * @return Map<Double, Boolean> - Keys of the returned map is the loan amount. It is equal to 0D if it is declined.
     * Values of the returned map is th Boolean value of the approval status of the application.
     * @throws IllegalArgumentException thrown if CustomerDto exists in database by ID but not equal to the entity.
     */
    @GetMapping ("/loan/apply")
    public Map<Double, Boolean> applyLoan(@Valid @RequestBody CustomerDto customerDto) {
        Long tckn = customerDto.getTckn();
        if(!customerService.existById(tckn)) {
            customerService.addCustomer(customerDto);
        } else if (customerDto.equals(toDto(customerService.getByTckn(tckn)))){
            throw new IllegalArgumentException("Provided TCKN exists in the database but is not equal to the entity.");
        }
        return loanService.applyLoan(tckn);
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
