package com.loanapp.loanapplication.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class CustomerDto {
    @Size(min = 11, max = 11)
    private Long tckn;
    private String name;
    private String lastName;
    private Double monthlySalary;
}
