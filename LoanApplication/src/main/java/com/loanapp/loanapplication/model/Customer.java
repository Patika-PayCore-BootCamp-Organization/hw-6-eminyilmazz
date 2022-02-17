package com.loanapp.loanapplication.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "tckn", nullable = false)
    @Digits(fraction = 0, integer = 11)
    private Long tckn;

    @Transient
    private Integer creditScore;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "monthly_salary")
    private double monthlySalary;

    @Transient
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Loan> loanList;
}
