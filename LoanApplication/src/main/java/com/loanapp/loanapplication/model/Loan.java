package com.loanapp.loanapplication.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_amount")
    private Double loanAmount;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_tckn", referencedColumnName = "tckn")
    private Customer customer;

    @Column(name = "approval_date")
    @CreationTimestamp
    private LocalDateTime approvalDate;

    public Loan(Double loanAmount, Customer customer) {
        this.loanAmount = loanAmount;
        this.customer = customer;
    }

    public Loan() {}
}
