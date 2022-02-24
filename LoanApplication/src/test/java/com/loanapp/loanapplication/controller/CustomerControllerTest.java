package com.loanapp.loanapplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.exception.handler.ApiExceptionHandler;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.Loan;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import com.loanapp.loanapplication.repository.CustomerRepository;
import com.loanapp.loanapplication.service.impl.CustomerServiceImpl;
import com.loanapp.loanapplication.service.impl.LoanServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static com.loanapp.loanapplication.model.dto.CustomerMapper.toDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private LoanServiceImpl loanService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerServiceImpl customerService;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());

        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void getAll() throws Exception {
        List<Customer> expectedCustomerList = getCustomerList();

        when(customerService.getAll()).thenReturn(expectedCustomerList);

        MockHttpServletResponse response = mockMvc.perform(get("/customer/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        List<Customer> actualCustomerList = new ObjectMapper().readValue(response.getContentAsString(), new TypeReference<List<Customer>>() {
        });
        assertEquals(expectedCustomerList, actualCustomerList);
    }

    @Test
    void getByTckn() throws Exception {
        Customer expectedCustomer = getCustomerList().get(0);

        when(customerService.getByTckn(10000000850L)).thenReturn(expectedCustomer);

        MockHttpServletResponse response = mockMvc.perform(get("/customer?tckn=" + expectedCustomer.getTckn()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

                Customer actualCustomer = new ObjectMapper().readValue(response.getContentAsString(), Customer.class);
                assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    void addCustomer() throws Exception {
        Customer addedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);
        String addedCustomerJson = mapToJson(addedCustomer);
        String URI  = "/customer/add";

        when(customerService.addCustomer(any(CustomerDto.class))).thenReturn(addedCustomer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(addedCustomerJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String responseString = response.getContentAsString();

        assertThat(responseString).isEqualTo(addedCustomerJson);
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void updateCustomer() throws Exception {
        Customer updatedCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);
        String updatedCustomerJson = mapToJson(updatedCustomer);
        String URI  = "/customer/update";

        when(customerService.updateCustomer(any(CustomerDto.class))).thenReturn(updatedCustomer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(URI)
                .accept(MediaType.APPLICATION_JSON).content(updatedCustomerJson)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String responseString = response.getContentAsString();

        assertThat(responseString).isEqualTo(updatedCustomerJson);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void deleteCustomer() throws Exception{
        String URI = "/customer/delete?tckn=12345678910";

        MockHttpServletResponse response = mockMvc.perform(delete(URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        String actualResponseStr = response.getContentAsString();
        Assert.assertEquals("Successfully deleted.", actualResponseStr);
    }

    @Test
    void applyLoan() throws Exception{
        String URI = "/customer/loan/apply?tckn=12345678910";
        Map<Double, Boolean> expectedMap = Collections.singletonMap(20000D, true);

        when(loanService.applyLoan(12345678910L)).thenReturn(Collections.singletonMap(20000D, true));
        MockHttpServletResponse response = mockMvc.perform(get(URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        assertEquals(mapToJson(expectedMap), response.getContentAsString());
    }

    @Test
    void getLoans() throws Exception{
        Customer customer = new Customer("Dummy", "Test",12345678910L, "1234567890",9000D);
        List<Loan> loanList = Arrays.asList(new Loan(1L, 20000D, customer, true),
                                            new Loan(2L, 10000D, customer, true),
                                            new Loan(3L, 10000D, customer, true));
        String URI = "/customer/loan/history";
        final ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("tckn", 12345678910L);
        objectNode.put("approved", true);
        String expectedJson = this.mapToJson(loanList);

        when(loanService.getLoans(objectNode)).thenReturn(loanList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON)
                                                                       .contentType(MediaType.APPLICATION_JSON)
                                                                       .content(this.mapToJson(objectNode));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String responseJson = result.getResponse().getContentAsString();

        assertEquals(expectedJson, responseJson);
    }

    private List<Customer> getCustomerList(){
        List<Customer> customerList = new ArrayList<>();
        customerList.add(new Customer("Ainsley","Hopper",10000000850L,"8794964085",5369D));
        customerList.add(new Customer("Thor","Parks",10000000950L,"8343458383",6676D));
        customerList.add(new Customer("Merritt","Woods",10000000050L,"6154776828",6858D));
        customerList.add(new Customer("Bruno","Avila",10000000810L,"3757713291",4934D));
        customerList.add(new Customer("Sacha","Ashley",10000000910L,"8542845257",3975D));
        customerList.add(new Customer("Hayes","Willis",10000000010L,"1724644137",4887D));
        return customerList;
    }
    /**
     * Maps an Object to a JSON String. Uses a Jackson ObjectMapper.
     */
    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
/*
    List<Loan> loanList = Arrays.asList(new Loan(1L,20000D, expectedCustomer, true),
            new Loan(2L,0D, expectedCustomer, false),
            new Loan(3L,10000D, expectedCustomer, true));*/