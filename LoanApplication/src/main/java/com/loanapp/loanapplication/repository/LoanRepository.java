package com.loanapp.loanapplication.repository;

import com.loanapp.loanapplication.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByCustomer_tckn(Long tckn);
}
