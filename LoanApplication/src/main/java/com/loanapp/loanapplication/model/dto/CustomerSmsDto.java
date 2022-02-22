package com.loanapp.loanapplication.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Validated
@Getter
@Setter
@Builder
public class CustomerSmsDto {
    private String name;
    private String lastName;
    @Pattern(regexp = "^[0-9]{10}", message = "Phone number needs to be 10 digits and can only contain only numbers.")
    private String phoneNumber;
    private double loanAmount;
    private LocalDateTime approvalDate;
}
