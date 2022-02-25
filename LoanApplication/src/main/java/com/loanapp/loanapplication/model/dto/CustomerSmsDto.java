package com.loanapp.loanapplication.model.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerSmsDto {
    private String name;
    private String lastName;
    @Pattern(regexp = "^[0-9]{10}", message = "Phone number needs to be 10 digits and can only contain only numbers.")
    private String phoneNumber;
    private double loanAmount;
    private String approvalDate;
}
