package com.loanapp.loanapplication.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Digits;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@Validated
@Builder
public class LoanDto {
    @PositiveOrZero
    private double loanAmount;
    @Digits(fraction = 0, integer = 11)
    private Long customerTckn;
    private LocalDateTime approvalDate;
    private String approvalStatus;
}
