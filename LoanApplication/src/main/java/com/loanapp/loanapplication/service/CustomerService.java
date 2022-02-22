package com.loanapp.loanapplication.service;

import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import org.springframework.http.ResponseEntity;

public interface CustomerService {

    Iterable<Customer> getAll();

    Customer getByTckn(Long tckn);

    ResponseEntity<CustomerDto> addCustomer(CustomerDto customerDto);

    Customer updateCustomer(CustomerDto customerDto);

    ResponseEntity<?> deleteCustomer(Long tckn);

    boolean existById(Long tckn);
}
