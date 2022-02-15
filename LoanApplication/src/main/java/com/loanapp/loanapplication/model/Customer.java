package com.loanapp.loanapplication.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "tckn", nullable = false)
    @Size(min = 11, max = 11)
    private Long tckn;

    @Transient
    private Integer creditScore;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "monthly_salary")
    private double monthlySalary;
}
