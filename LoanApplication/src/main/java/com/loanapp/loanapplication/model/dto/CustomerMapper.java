package com.loanapp.loanapplication.model.dto;

import com.loanapp.loanapplication.model.Customer;

public class CustomerMapper {
    private CustomerMapper(){}
    public static CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .tckn(customer.getTckn())
                .name(customer.getName())
                .lastName(customer.getLastName())
                .monthlySalary(customer.getMonthlySalary())
                .build();
    }
    public static Customer toEntity(CustomerDto customerDto){
        return Customer.builder()
                .tckn(customerDto.getTckn())
                .name(customerDto.getName())
                .lastName(customerDto.getLastName())
                .monthlySalary(customerDto.getMonthlySalary())
                .build();
    }
}
