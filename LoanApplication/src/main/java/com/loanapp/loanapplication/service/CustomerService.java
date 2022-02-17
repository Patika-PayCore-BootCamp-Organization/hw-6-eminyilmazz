package com.loanapp.loanapplication.service;

import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import org.springframework.http.ResponseEntity;

public interface CustomerService {

    Iterable<CustomerDto> getAll();

    ResponseEntity<Customer> addCustomer(CustomerDto customerDto);

    Customer updateCustomer(CustomerDto customerDto);

    Customer findById(Long tckn);
}
