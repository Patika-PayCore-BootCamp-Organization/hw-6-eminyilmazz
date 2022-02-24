package com.loanapp.loanapplication.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.exception.IllegalTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
import com.loanapp.loanapplication.exception.TcknValidator;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.Loan;
import com.loanapp.loanapplication.repository.LoanRepository;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {
    @Mock
    private CustomerServiceImpl customerService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private TcknValidator tcknValidator;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void applyLoan_with1000CreditScore_And9000Salary() {
        Customer customer = new Customer("Dummy", "Test",12345678910L, "1234567890",9000D);
        Loan expectedLoan = new Loan(1L, 36000D, customer, true);
        Map<Double, Boolean> expectedLoanMap = new HashMap<>();
        expectedLoanMap.put(36000D, true);

        when(customerService.getByTckn(customer.getTckn())).thenReturn(customer);

        Map<Double, Boolean> actualLoanMap = loanService.applyLoan(customer.getTckn());

        ArgumentCaptor<Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);

        verify(loanRepository).save(loanArgumentCaptor.capture());

        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertEquals(expectedLoan.getLoanAmount(), capturedLoan.getLoanAmount());
        assertEquals(expectedLoan.getCustomer().getTckn(), capturedLoan.getCustomer().getTckn());
        assertEquals(expectedLoan.isApprovalStatus(), capturedLoan.isApprovalStatus());
        assertEquals(expectedLoanMap, actualLoanMap);
        verify(loanRepository, times(1)).save(any());
    }

    @Test
    void applyLoan_with800CreditScore_And9000Salary() {
        Customer customer = new Customer("Dummy", "Test",12345678800L, "1234567890",9000D);
        Loan expectedLoan = new Loan(1L, 20000D, customer, true);
        Map<Double, Boolean> expectedLoanMap = new HashMap<>();
        expectedLoanMap.put(20000D, true);

        when(customerService.getByTckn(customer.getTckn())).thenReturn(customer);

        Map<Double, Boolean> actualLoanMap = loanService.applyLoan(customer.getTckn());

        ArgumentCaptor<Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);

        verify(loanRepository).save(loanArgumentCaptor.capture());

        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertEquals(expectedLoan.getLoanAmount(), capturedLoan.getLoanAmount());
        assertEquals(expectedLoan.getCustomer().getTckn(), capturedLoan.getCustomer().getTckn());
        assertEquals(expectedLoan.isApprovalStatus(), capturedLoan.isApprovalStatus());
        assertEquals(expectedLoanMap, actualLoanMap);
        verify(loanRepository, times(1)).save(any());
    }

    @Test
    void applyLoan_with200CreditScore_And9000Salary() {
        Customer customer = new Customer("Dummy", "Test",12345678200L, "1234567890",9000D);
        Map<Double, Boolean> expectedLoanMap = new HashMap<>();
        expectedLoanMap.put(0D, false);

        when(customerService.getByTckn(customer.getTckn())).thenReturn(customer);

        Map<Double, Boolean> actualLoanMap = loanService.applyLoan(customer.getTckn());

        assertEquals(expectedLoanMap, actualLoanMap);

        verify(loanRepository, never()).save(any());
    }

    @Test
    void applyLoan_with10000CreditScore_And3000Salary() {
        Customer customer = new Customer("Dummy", "Test",12345678910L, "1234567890",3000D);
        Loan expectedLoan = new Loan(1L, 12000D, customer, true);
        Map<Double, Boolean> expectedLoanMap = new HashMap<>();
        expectedLoanMap.put(12000D, true);

        when(customerService.getByTckn(customer.getTckn())).thenReturn(customer);

        Map<Double, Boolean> actualLoanMap = loanService.applyLoan(customer.getTckn());

        ArgumentCaptor<Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);

        verify(loanRepository).save(loanArgumentCaptor.capture());

        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertEquals(expectedLoan.getLoanAmount(), capturedLoan.getLoanAmount());
        assertEquals(expectedLoan.getCustomer().getTckn(), capturedLoan.getCustomer().getTckn());
        assertEquals(expectedLoan.isApprovalStatus(), capturedLoan.isApprovalStatus());
        assertEquals(expectedLoanMap, actualLoanMap);
        verify(loanRepository, times(1)).save(any());
    }

    @Test
    void applyLoan_with8000CreditScore_And3000Salary() {
        Customer customer = new Customer("Dummy", "Test",12345678800L, "1234567890",3000D);
        Loan expectedLoan = new Loan(1L, 10000D, customer, true);
        Map<Double, Boolean> expectedLoanMap = new HashMap<>();
        expectedLoanMap.put(10000D, true);

        when(customerService.getByTckn(customer.getTckn())).thenReturn(customer);

        Map<Double, Boolean> actualLoanMap = loanService.applyLoan(customer.getTckn());

        ArgumentCaptor<Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);

        verify(loanRepository).save(loanArgumentCaptor.capture());

        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertEquals(expectedLoan.getLoanAmount(), capturedLoan.getLoanAmount());
        assertEquals(expectedLoan.getCustomer().getTckn(), capturedLoan.getCustomer().getTckn());
        assertEquals(expectedLoan.isApprovalStatus(), capturedLoan.isApprovalStatus());
        assertEquals(expectedLoanMap, actualLoanMap);
        verify(loanRepository, times(1)).save(any());
    }

    @Test
    void applyLoan_with200CreditScore_And3000Salary() {
        Customer customer = new Customer("Dummy", "Test",12345678200L, "1234567890",3000D);
        Map<Double, Boolean> expectedLoanMap = new HashMap<>();
        expectedLoanMap.put(0D, false);

        when(customerService.getByTckn(customer.getTckn())).thenReturn(customer);

        Map<Double, Boolean> actualLoanMap = loanService.applyLoan(customer.getTckn());

        assertEquals(expectedLoanMap, actualLoanMap);

        verify(loanRepository, never()).save(any());
    }

    @Test
    void applyLoan_CustomerNotExist_ThrowNotFoundException() {
        when(customerService.getByTckn(12345678910L)).thenThrow(new NotFoundException("Customer tckn: " + 12345678910L + " not found!"));

        assertThatThrownBy(() -> loanService.applyLoan(12345678910L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer tckn: " + 12345678910L + " not found!");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanApplicationProcessor_With1000CreditScore_AndAbove5000Salary() {
        Integer creditScore = 1000;
        Double salary = 8000D;
        Map<Double, Boolean> expectedLoan = new HashMap<>();
        expectedLoan.put(32000D, true);
        Map<Double, Boolean> processedLoan;
        processedLoan = loanService.loanApplicationProcessor(creditScore, salary);

        assertEquals(expectedLoan, processedLoan);
    }

    @Test
    void loanApplicationProcessor_With800CreditScore_AndAbove5000Salary() {
        Integer creditScore = 800;
        Double salary = 8000D;
        Map<Double, Boolean> expectedLoan = new HashMap<>();
        expectedLoan.put(20000D, true);
        Map<Double, Boolean> processedLoan;
        processedLoan = loanService.loanApplicationProcessor(creditScore, salary);

        assertEquals(expectedLoan, processedLoan);
    }

    @Test
    void loanApplicationProcessor_With250CreditScore_AndAbove5000Salary() {
        Integer creditScore = 250;
        Double salary = 8000D;
        Map<Double, Boolean> expectedLoan = new HashMap<>();
        expectedLoan.put(0D, false);
        Map<Double, Boolean> processedLoan;
        processedLoan = loanService.loanApplicationProcessor(creditScore, salary);

        assertEquals(expectedLoan, processedLoan);
    }

    @Test
    void loanApplicationProcessor_With1000CreditScore_AndBelow5000Salary() {
        Integer creditScore = 1000;
        Double salary = 3000D;
        Map<Double, Boolean> expectedLoan = new HashMap<>();
        expectedLoan.put(12000D, true);
        Map<Double, Boolean> processedLoan;
        processedLoan = loanService.loanApplicationProcessor(creditScore, salary);

        assertEquals(expectedLoan, processedLoan);
    }

    @Test
    void loanApplicationProcessor_With800CreditScore_AndBelow5000Salary() {
        Integer creditScore = 800;
        Double salary = 3000D;
        Map<Double, Boolean> expectedLoan = new HashMap<>();
        expectedLoan.put(10000D, true);
        Map<Double, Boolean> processedLoan;
        processedLoan = loanService.loanApplicationProcessor(creditScore, salary);

        assertEquals(expectedLoan, processedLoan);
    }

    @Test
    void loanApplicationProcessor_With250CreditScore_AndBelow5000Salary() {
        Integer creditScore = 250;
        Double salary = 3000D;
        Map<Double, Boolean> expectedLoan = new HashMap<>();
        expectedLoan.put(0D, false);
        Map<Double, Boolean> processedLoan;
        processedLoan = loanService.loanApplicationProcessor(creditScore, salary);

        assertEquals(expectedLoan, processedLoan);
    }

    @Test
    void getLoans_WhenObjectNodeHasApprovedStatusFalse() {
        Customer customer = new Customer("Dummy", "Test",12345678910L, "1234567890",9000D);
        List<Loan> expectedLoanList = Arrays.asList(new Loan(1L, 20000D, customer, true),
                new Loan(2L, 10000D, customer, true),
                new Loan(3L, 10000D, customer, true),
                new Loan(4L, 0D, customer, false),
                new Loan(5L, 0D, customer, false));
        final ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("tckn", 12345678910L);
        objectNode.put("approved", false);

        when(loanRepository.findAllByCustomer_tckn(objectNode.get("tckn").asLong())).thenReturn(expectedLoanList);
        when(customerService.existById(objectNode.get("tckn").asLong())).thenReturn(true);

        List<Loan> actualLoanList = loanService.getLoans(objectNode);

        assertEquals(expectedLoanList.size(), actualLoanList.size());

        for (Loan expectedLoan : expectedLoanList) {
            Optional<Loan> actualLoan = actualLoanList.stream()
                    .filter((actual) -> actual.getId() == expectedLoan.getId())
                    .findFirst();
            assertEquals(expectedLoan.isApprovalStatus(), actualLoan.get().isApprovalStatus());
            assertEquals(expectedLoan.getLoanAmount(), actualLoan.get().getLoanAmount());
            assertEquals(expectedLoan.getCustomer(), actualLoan.get().getCustomer());
        }
    }

    @Test
    void getLoans_WhenObjectNodeDoesNotHaveApprovedStatusFalse() {
        Customer customer = new Customer("Dummy", "Test",12345678910L, "1234567890",9000D);
        List<Loan> expectedLoanList = Arrays.asList(new Loan(1L, 20000D, customer, true),
                new Loan(2L, 10000D, customer, true),
                new Loan(3L, 10000D, customer, true),
                new Loan(4L, 0D, customer, false),
                new Loan(5L, 0D, customer, false));
        Long tckn = 12345678910L;
        final ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("tckn", tckn);

        when(loanRepository.findAllByCustomer_tckn(12345678910L)).thenReturn(expectedLoanList);
        when(customerService.existById(12345678910L)).thenReturn(true);

        List<Loan> actualLoanList = loanService.getLoans(objectNode);

        assertEquals(expectedLoanList.size(), actualLoanList.size());

        for (Loan expectedLoan : expectedLoanList) {
            Optional<Loan> actualLoan = actualLoanList.stream()
                    .filter((actual) -> actual.getId() == expectedLoan.getId())
                    .findFirst();
            assertEquals(expectedLoan.isApprovalStatus(), actualLoan.get().isApprovalStatus());
            assertEquals(expectedLoan.getLoanAmount(), actualLoan.get().getLoanAmount());
            assertEquals(expectedLoan.getCustomer(), actualLoan.get().getCustomer());
        }
    }

    @Test
    void getLoans_WhenObjectNodeHasApprovedStatusTrue(){
        Customer customer = new Customer("Dummy", "Test",12345678910L, "1234567890",9000D);
        List<Loan> allLoanList = Arrays.asList(new Loan(1L, 20000D, customer, true),
                                               new Loan(2L, 10000D, customer, true),
                                               new Loan(3L, 10000D, customer, true),
                                               new Loan(4L, 0D, customer, false),
                                               new Loan(5L, 0D, customer, false));

        List<Loan> expectedLoanList = Arrays.asList(new Loan(1L, 20000D, customer, true),
                                                    new Loan(2L, 10000D, customer, true),
                                                    new Loan(3L, 10000D, customer, true));

        Long tckn = 12345678910L;
        final ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("tckn", tckn);
        objectNode.put("approved", true);

        when(loanRepository.findAllByCustomer_tckn(12345678910L)).thenReturn(allLoanList);
        when(customerService.existById(12345678910L)).thenReturn(true);

        List<Loan> actualLoanList = loanService.getLoans(objectNode);

        assertEquals(expectedLoanList.size(), actualLoanList.size());

        for (Loan expectedLoan : expectedLoanList) {
            Optional<Loan> actualLoan = actualLoanList.stream()
                    .filter((actual) -> actual.getId() == expectedLoan.getId())
                    .findFirst();
            assertEquals(expectedLoan.isApprovalStatus(), actualLoan.get().isApprovalStatus());
            assertEquals(expectedLoan.getLoanAmount(), actualLoan.get().getLoanAmount());
            assertEquals(expectedLoan.getCustomer(), actualLoan.get().getCustomer());
        }
    }

    @Test
    void getLoans_WhenObjectNodeDoesNotHaveTckn_ThrowIllegalArgumentException() {
        final ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();



        assertThatThrownBy(() ->  loanService.getLoans(objectNode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provided body is not valid.\nBody needs to have an 11 digits value for TCKN." +
                        "\nExample:\n{\n\"tckn\" : \"12345678910\",\n\"approved\" : true\"\n}");
        verify(loanRepository, never()).findAllByCustomer_tckn(any());
    }

    @Test
    void getLoans_WhenTcknDoesCannotBeValidated_ThrowIllegalTcknException() {
        final ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("tckn", 1234L);


        assertThatThrownBy(() ->  loanService.getLoans(objectNode))
                .isInstanceOf(IllegalTcknException.class)
                .hasMessageContaining("TCKN needs to be 11 digits and can only contain only numbers.");
        verify(loanRepository, never()).findAllByCustomer_tckn(any());
    }

    @Test
    void getLoans_WhenTcknDoesNotExist_ThrowNotFoundException() {
        final ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("tckn", 12345678910L);

        when(customerService.existById(12345678910L)).thenReturn(false);
        assertThatThrownBy(() ->  loanService.getLoans(objectNode))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer tckn: " + 12345678910L + " not found!");
        verify(loanRepository, never()).findAllByCustomer_tckn(any());
    }

    @Test
    void getApprovedLoansById() {
        Customer customer = new Customer("Dummy", "Test",12345678910L, "1234567890",9000D);

        List<Loan> allLoanList = Arrays.asList(new Loan(1L, 20000D, customer, true),
                new Loan(2L, 10000D, customer, true),
                new Loan(3L, 10000D, customer, true),
                new Loan(4L, 0D, customer, false),
                new Loan(5L, 0D, customer, false));

        List<Loan> expectedLoanList = Arrays.asList(new Loan(1L, 20000D, customer, true),
                new Loan(2L, 10000D, customer, true),
                new Loan(3L, 10000D, customer, true));

        Long tckn = 12345678910L;

        when(customerService.existById(tckn)).thenReturn(true);
        when(loanRepository.findAllByCustomer_tckn(12345678910L)).thenReturn(allLoanList);

        List<Loan> actualLoanList = loanService.getApprovedLoansById(tckn);

        assertEquals(expectedLoanList.size(), actualLoanList.size());

        for (Loan expectedLoan : expectedLoanList) {
            Optional<Loan> actualLoan = actualLoanList.stream()
                    .filter((actual) -> actual.getId() == expectedLoan.getId())
                    .findFirst();
            assertEquals(expectedLoan.isApprovalStatus(), actualLoan.get().isApprovalStatus());
            assertEquals(expectedLoan.getLoanAmount(), actualLoan.get().getLoanAmount());
            assertEquals(expectedLoan.getCustomer(), actualLoan.get().getCustomer());
        }
    }

    @Test
    void getApprovedLoansById_WhenCustomerDoesNotExist_ThrowNotFoundException() {
        Long tckn = 12345678910L;

        when(customerService.existById(tckn)).thenReturn(false);

        assertThatThrownBy(() -> loanService.getApprovedLoansById(tckn))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer tckn: " + tckn + " not found!");

        verify(loanRepository, never()).findAllByCustomer_tckn(any());

    }

    @Test
    void getAllLoansById() {
        Customer customer = new Customer("Dummy", "Test",12345678910L, "1234567890",9000D);

        List<Loan> expectedLoanList = Arrays.asList(new Loan(1L, 20000D, customer, true),
                new Loan(2L, 10000D, customer, true),
                new Loan(3L, 10000D, customer, true),
                new Loan(4L, 0D, customer, false),
                new Loan(5L, 0D, customer, false));

        Long tckn = 12345678910L;

        when(customerService.existById(tckn)).thenReturn(true);
        when(loanRepository.findAllByCustomer_tckn(12345678910L)).thenReturn(expectedLoanList);

        List<Loan> actualLoanList = loanService.getAllLoansById(tckn);

        assertEquals(expectedLoanList.size(), actualLoanList.size());

        for (Loan expectedLoan : expectedLoanList) {
            Optional<Loan> actualLoan = actualLoanList.stream()
                    .filter((actual) -> actual.getId() == expectedLoan.getId())
                    .findFirst();
            assertEquals(expectedLoan.isApprovalStatus(), actualLoan.get().isApprovalStatus());
            assertEquals(expectedLoan.getLoanAmount(), actualLoan.get().getLoanAmount());
            assertEquals(expectedLoan.getCustomer(), actualLoan.get().getCustomer());
        }
    }

    @Test
    void getAllLoansById_WhenCustomerDoesNotExist_ThrowNotFoundException(){
        Long tckn = 12345678910L;

        when(customerService.existById(tckn)).thenReturn(false);

        assertThatThrownBy(() -> loanService.getAllLoansById(tckn))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer tckn: " + tckn + " not found!");

        verify(loanRepository, never()).findAllByCustomer_tckn(any());
    }
}