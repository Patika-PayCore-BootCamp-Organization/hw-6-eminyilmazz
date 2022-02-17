package com.loanapp.loanapplication.controller;

import com.loanapp.loanapplication.exception.IllegalTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import com.loanapp.loanapplication.service.CustomerService;
import com.loanapp.loanapplication.service.LoanService;
import com.loanapp.loanapplication.service.impl.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LoanService loanService;

    @GetMapping("/all")
    public Iterable<CustomerDto> getAll(){
        return customerService.getAll();
    }
    @PostMapping("/add")
    public ResponseEntity<Customer> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
        return customerService.addCustomer(customerDto);
    }
    @PutMapping("/update")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody CustomerDto customerDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(customerDto));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping ("/loan")
    public Map<Double, Boolean> applyLoan(@RequestParam(name = "tckn") Long tckn) {
        try {
            return loanService.applyLoan(tckn);
        } catch (IllegalTcknException e){
            return Collections.singletonMap(0D, false);
        } catch (NotFoundException e) {
            return Collections.singletonMap(0D, false);
        }
    }
}
