package com.loanapp.loanapplication.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Digits;

@Validated
@Data
@Builder
public class CustomerDto {
    @Digits(fraction = 0, integer = 11)
    private Long tckn;
    private String name;
    private String lastName;
    private Double monthlySalary;
}
