package com.loanapp.loanapplication.repository;

import com.loanapp.loanapplication.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Transactional
    List<Loan> findAllByCustomer_tckn(Long tckn);

    @Transactional
    void deleteAllByCustomer_Tckn(Long tckn);
}
