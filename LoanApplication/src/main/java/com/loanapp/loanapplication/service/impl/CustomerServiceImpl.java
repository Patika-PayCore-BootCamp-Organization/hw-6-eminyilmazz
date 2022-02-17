package com.loanapp.loanapplication.service.impl;

import com.loanapp.loanapplication.exception.NotFoundException;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import com.loanapp.loanapplication.model.dto.CustomerMapper;
import com.loanapp.loanapplication.repository.CustomerRepository;
import com.loanapp.loanapplication.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static com.loanapp.loanapplication.model.dto.CustomerMapper.toEntity;

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
                .body(customerRepository.save(toEntity(customerDto)));
    }

    @Override
    public Customer updateCustomer(CustomerDto customerDto) throws NotFoundException {
        if (!customerRepository.existsById(customerDto.getTckn())) {
            throw new NotFoundException("Customer tckn: " + customerDto.getTckn() + " not found!");
        }
        return customerRepository.save(toEntity(customerDto));
    }

    @Override
    public Customer findById(Long tckn) throws NotFoundException{
        return customerRepository.findById(tckn)
                                 .orElseThrow(() -> new NotFoundException("Customer tckn: " + tckn + " not found!"));
    }

//    @Override
//    public Map<Double, Boolean> applyLoan(Long tckn) {
//        if (tckn.toString().length() != 11) {
//            throw new IllegalTcknException("TCKN must be an 11 digits number!");
//        }
//        Optional<Customer> optionalCustomer = customerRepository.findById(tckn);
//        if (!optionalCustomer.isPresent()) {
//            throw new NotFoundException("Customer not found by provided TCKN");
//        } else {
//            Customer customer = optionalCustomer.get();
//            Map<Double, Boolean> loanApplicationResponse = loanApplicationProcessor(customer.getCreditScore(),
//                    customer.getMonthlySalary());
//            if(loanApplicationResponse.containsValue(true)) {
//
//            }
//            return loanApplicationResponse;
//        }
//    }
//
//    @Override
//    public Map<Double, Boolean> loanApplicationProcessor(Integer creditScore, Double monthlySalary) {
//        final Double CREDIT_LIMIT_MULTIPLIER = 4D;
//        Double loanAmount = 0D;
//        if(creditScore <= 500) {
//            return Collections.singletonMap(loanAmount, false);
//        } else if(creditScore != 1000) {
//            loanAmount = monthlySalary < 5000 ? 10000D : 20000D;
//            return Collections.singletonMap(loanAmount, true);
//        } else {
//            return Collections.singletonMap(CREDIT_LIMIT_MULTIPLIER * monthlySalary, true);
//        }
//    }
}
