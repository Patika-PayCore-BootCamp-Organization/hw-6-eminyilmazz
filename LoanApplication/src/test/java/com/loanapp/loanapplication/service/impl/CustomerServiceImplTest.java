package com.loanapp.loanapplication.service.impl;

import com.loanapp.loanapplication.exception.DuplicateTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import com.loanapp.loanapplication.repository.CustomerRepository;
import org.hibernate.annotations.NotFound;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.loanapp.loanapplication.model.dto.CustomerMapper.toDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void getAll() {
        List<Customer> expectedCustomerList = Arrays.asList(
                new Customer("Ainsley", "Hopper",10000000850L, "8794964085",5369D),
                new Customer("Thor","Parks",10000000950L,"8343458383",6676),
                new Customer("Merritt","Woods",10000000050L,"6154776828",6858),
                new Customer("Bruno","Avila",10000000810L,"3757713291",4934),
                new Customer("Sacha","Ashley",10000000910L,"8542845257",3975),
                new Customer("Hayes","Willis",10000000010L,"1724644137",4887)
        );

        when(customerRepository.findAll()).thenReturn(expectedCustomerList);

        List<Customer> actualCustomerList = customerService.getAll();

        assertEquals(expectedCustomerList.size(), actualCustomerList.size());

        for (Customer expectedCustomer: expectedCustomerList) {
            Optional<Customer> actualCustomer = actualCustomerList.stream()
                    .filter((actual) -> actual.getTckn() == expectedCustomer.getTckn()).findFirst();

            assertEquals(expectedCustomer.getName() + expectedCustomer.getLastName(),
                                      actualCustomer.get().getName() + actualCustomer.get().getLastName());

            assertEquals(expectedCustomer.getPhoneNumber(), actualCustomer.get().getPhoneNumber());

            assertEquals(expectedCustomer.getMonthlySalary(), actualCustomer.get().getMonthlySalary());
        }
    }

    @Test
    void getByTckn() {
        Customer expectedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        when(customerRepository.findById(12345678910L)).thenReturn(Optional.of(expectedCustomer));

        Customer actualCustomer = customerService.getByTckn(12345678910L);

        assertEquals(expectedCustomer, actualCustomer);
    }
    @Test
    void getByTckn_NotExist_ThrowsNotFoundException() {
        Customer expectedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        when(customerRepository.findById(12345678910L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getByTckn(expectedCustomer.getTckn()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer tckn: " + expectedCustomer.getTckn() + " not found!");
    }

    @Test
    void addCustomer() {
        Customer addedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        customerService.addCustomer(toDto(addedCustomer));

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer).isEqualTo(addedCustomer);

        verify(customerRepository, times(1)).save(any());
    }

    @Test
    void addCustomer_TcknAlreadyExists_ThrowDuplicateTcknException() {
        Customer addedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        when(customerRepository.existsById(12345678910L)).thenReturn(true);

        assertThatThrownBy(() -> customerService.addCustomer(toDto(addedCustomer)))
                .isInstanceOf(DuplicateTcknException.class)
                .hasMessageContaining("Provided TCKN already exists.\nCannot accept duplicate TCKN.\n");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer() {
        Customer updatedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        when(customerRepository.existsById(updatedCustomer.getTckn())).thenReturn(true);

        customerService.updateCustomer(toDto(updatedCustomer));

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer).isEqualTo(updatedCustomer);

        verify(customerRepository, times(1)).save(any());
    }

    @Test
    void updateCustomer_NotExist_ThrowNotFoundException() {
        Customer updatedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        when(customerRepository.existsById(updatedCustomer.getTckn())).thenReturn(false);

        assertThatThrownBy(() -> customerService.updateCustomer(toDto(updatedCustomer)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer tckn: " + updatedCustomer.getTckn() + " not found!");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void deleteCustomer() {
        Customer deletedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        when(customerRepository.existsById(deletedCustomer.getTckn())).thenReturn(true);

        customerService.deleteCustomer(deletedCustomer.getTckn());

        verify(customerRepository, times(1)).deleteById(deletedCustomer.getTckn());
    }

    @Test
    void deleteCustomer_NotExist_ThrowNotFoundException() {
        Customer deletedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        when(customerRepository.existsById(deletedCustomer.getTckn())).thenReturn(false);

        assertThatThrownBy(() -> customerService.deleteCustomer(deletedCustomer.getTckn()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Delete operation is not successful.\nThe customer does not exist.");

        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void existById() {
        Customer existingCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        when(customerRepository.existsById(existingCustomer.getTckn())).thenReturn(true);

        customerService.existById(existingCustomer.getTckn());

        verify(customerRepository, times(1)).existsById(existingCustomer.getTckn());
    }
}