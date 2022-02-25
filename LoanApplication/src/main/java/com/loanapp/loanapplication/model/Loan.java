package com.loanapp.loanapplication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Setter
@Builder
@Entity
@Table(name = "loans")
public class Loan {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_amount")
    @ApiModelProperty(name = "Loan amount", required = true, notes = "MUST be double, amount of the loan", example = "1234")
    private Double loanAmount;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_tckn", referencedColumnName = "tckn")
    @ApiModelProperty(name = "Customer TCKN", required = true, notes = "MUST be 11 digits number, Refers to owner of the loan", example = "12345678910")
    private Customer customer;

    @Column(name = "approval_date")
    @CreationTimestamp
    @ApiModelProperty(name = "Approval date", required = true, notes = "The date for loan approval", example = "12345678910")
    private LocalDateTime approvalDate;

    @Column(name = "approval_status")
    @NotNull
    private boolean approvalStatus;

    public Loan(Long id, Double loanAmount, Customer customer, boolean approvalStatus) {
        this.id = id;
        this.loanAmount = loanAmount;
        this.customer = customer;
        this.approvalStatus = approvalStatus;
    }

    public Loan(LocalDateTime approvalDate, boolean approvalStatus, Double loanAmount, Customer customer){
        this.approvalDate = approvalDate;
        this.approvalStatus = approvalStatus;
        this.loanAmount = loanAmount;
        this.customer = customer;
    }

    public Loan() {}
}
