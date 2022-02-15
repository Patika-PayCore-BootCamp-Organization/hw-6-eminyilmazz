package com.loanapp.loanapplication.service.impl;

import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import com.loanapp.loanapplication.model.dto.CustomerMapper;
import com.loanapp.loanapplication.repository.CustomerRepository;
import com.loanapp.loanapplication.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Iterable<CustomerDto> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<Customer> addCustomer(CustomerDto customerDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(customerRepository.save(CustomerMapper.toEntity(customerDto)));
    }
}
