package com.loanapp.loanapplication.model.dto;

import com.loanapp.loanapplication.model.Loan;

public class LoanMapper {
    public static LoanDto toDto(Loan loan){
        return LoanDto.builder()
                .approvalDate(loan.getApprovalDate())
                .customerTckn(loan.getCustomer().getTckn())
                .approvalStatus(loan.isApprovalStatus() ? "Approved" : "Denied")
                .loanAmount(loan.getLoanAmount())
                .build();
    }
    public static Loan toSimpleEntity(LoanDto loanDto) {
        return Loan.builder()
                .loanAmount(loanDto.getLoanAmount())
                .approvalStatus(loanDto.getApprovalStatus().equals("Approved"))
                .approvalDate(loanDto.getApprovalDate())
                .build();
    }
}
