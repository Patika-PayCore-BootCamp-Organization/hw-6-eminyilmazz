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
    /**
     * Gets all customers from repository.
     * @return Iterable<Customer>
     */
    @Override
    public Iterable<Customer> getAll() {
        return customerRepository.findAll();
    }
    /**
     * Get method for Customer.
     * @param tckn in Long type.
     * @return Customer, for the provided tckn.
     * @throws NotFoundException - thrown if a Customer does not exist for the provided tckn.
     */
    @Override
    public Customer getByTckn(Long tckn) {
        return customerRepository.findById(tckn)
                                 .orElseThrow(() -> new NotFoundException("Customer tckn: " + tckn + " not found!"));
    }
    /**
     * This method adds a Customer to the customer repository.
     * @param customerDto
     * @return ResponseEntity<CustomerDto> - If adding customer is successful, status is CREATED, else BAD_REQUEST.
     * @throws DuplicateTcknException - thrown if the provided CustomerDto already exists in the repository by its tckn field.
     */
    @Override
    public ResponseEntity<CustomerDto> addCustomer(CustomerDto customerDto) throws DuplicateTcknException {
        if (customerRepository.existsById(customerDto.getTckn())) {
            throw new DuplicateTcknException();
        }
        Customer customer = customerRepository.save(toEntity(customerDto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDto(customer));
    }
    /**
     * Updates an already existing Customer.
     * @param customerDto
     * @return Customer - Returns the Customer if successful.
     * @throws NotFoundException - thrown if Customer does not exist in the repository.
     */
    @Override
    public Customer updateCustomer(CustomerDto customerDto) throws NotFoundException {
        if (!customerRepository.existsById(customerDto.getTckn())) {
            throw new NotFoundException("Customer tckn: " + customerDto.getTckn() + " not found!");
        }
        return customerRepository.save(toEntity(customerDto));
    }
    /**
     * Delete method for an existing Customer.
     * @param tckn of the customer to be deleted.
     * @return A generic ResponseEntity<> - Returns an OK status if deletion is successful.
     * @throws NotFoundException - if a Customer does not exist for the provided tckn.
     */
    @Override
    public ResponseEntity<?> deleteCustomer(Long tckn) {
        if (!customerRepository.existsById(tckn)){
            throw new NotFoundException("Delete operation is not successful.\nThe customer does not exist.");
        }
        customerRepository.deleteById(tckn);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted.");
    }
    /**
     * A method that checks repository if a customer exists for the provided tckn.
     * This method is used by LoanService. This helps to keep LoanService away from CustomerRepository.
     * @param tckn of the customer to be checked if it exists.
     * @return boolean value. True if customer exist, false if it does not.
     */
    @Override
    public boolean existById(Long tckn) {
        return customerRepository.existsById(tckn);
    }
}
