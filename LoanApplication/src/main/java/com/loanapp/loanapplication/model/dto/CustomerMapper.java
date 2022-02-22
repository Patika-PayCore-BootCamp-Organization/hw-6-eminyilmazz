package com.loanapp.loanapplication.model.dto;

import com.loanapp.loanapplication.model.Customer;

public class CustomerMapper {
    private CustomerMapper(){}
    public static CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .tckn(customer.getTckn())
                .name(customer.getName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .monthlySalary(customer.getMonthlySalary())
                .build();
    }
    public static Customer toEntity(CustomerDto customerDto){
        return Customer.builder()
                .tckn(customerDto.getTckn())
                .name(customerDto.getName())
                .lastName(customerDto.getLastName())
                .phoneNumber(customerDto.getPhoneNumber())
                .monthlySalary(customerDto.getMonthlySalary())
                .build();
    }
    public static CustomerSmsDto toSmsDto(Customer customer) {
        return CustomerSmsDto.builder()
                .name(customer.getName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
