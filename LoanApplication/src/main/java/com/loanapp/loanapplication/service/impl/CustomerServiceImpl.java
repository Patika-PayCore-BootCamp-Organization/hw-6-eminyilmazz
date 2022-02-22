package com.loanapp.loanapplication.service.impl;

import com.loanapp.loanapplication.exception.DuplicateTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import com.loanapp.loanapplication.repository.CustomerRepository;
import com.loanapp.loanapplication.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.loanapp.loanapplication.model.dto.CustomerMapper.toDto;
import static com.loanapp.loanapplication.model.dto.CustomerMapper.toEntity;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Iterable<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getByTckn(Long tckn) {
        return customerRepository.findById(tckn)
                                 .orElseThrow(() -> new NotFoundException("Customer tckn: " + tckn + " not found!"));
    }
    @Override
    public ResponseEntity<CustomerDto> addCustomer(CustomerDto customerDto) throws DuplicateTcknException {
        if (customerRepository.existsById(customerDto.getTckn())) {
            throw new DuplicateTcknException();
        }
        Customer customer = customerRepository.save(toEntity(customerDto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDto(customer));
    }

    @Override
    public Customer updateCustomer(CustomerDto customerDto) throws NotFoundException {
        if (!customerRepository.existsById(customerDto.getTckn())) {
            throw new NotFoundException("Customer tckn: " + customerDto.getTckn() + " not found!");
        }
        return customerRepository.save(toEntity(customerDto));
    }

    @Override
    public ResponseEntity<?> deleteCustomer(Long tckn) {
        if (!customerRepository.existsById(tckn)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete operation is not successful.\nThe customer does not exist.");
        }
        customerRepository.deleteById(tckn);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted.");
    }

    @Override
    public boolean existById(Long tckn) {
        return customerRepository.existsById(tckn);
    }
}
