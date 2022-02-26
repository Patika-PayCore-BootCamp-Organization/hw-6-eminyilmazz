package com.loanapp.loanapplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.LoanApplication;
import com.loanapp.loanapplication.exception.DuplicateTcknException;
import com.loanapp.loanapplication.exception.IllegalTcknException;
import com.loanapp.loanapplication.exception.NotFoundException;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.Loan;
import com.loanapp.loanapplication.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private HttpHeaders headers = new HttpHeaders();
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Disabled("Disabled due to handling issues for Customer/Loan and Loan/LocalDateTime relations")
    @Test
    void getAll() throws JsonProcessingException {
        String customersJson = mapToJson(allCustomers);
        String _all = "/customer/all";

        String responseBodyAsJson = testRestTemplate.getForObject(formFullURLWithPort(_all), String.class);
        assertThat(responseBodyAsJson).isEqualTo(customersJson);
    }

    @Disabled("Disabled due to handling issues for Customer/Loan and Loan/LocalDateTime relations")
    @Test
    void getByTckn() throws JsonProcessingException {
        Customer customer = allCustomers.get(0);
        customer.setLoanList(loanListOf850);
        String _gettckn = "/customer/get?tckn=10000000850";

        String responseJson = testRestTemplate.getForObject(formFullURLWithPort(_gettckn), String.class);
        assertThat(mapToJson(customer)).isEqualTo(responseJson);
    }

    @Test
    void addCustomer() throws JsonProcessingException {
        Customer newCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        String _add = "/customer/add";

        String customerJson = this.mapToJson(newCustomer);

        HttpEntity<Customer> httpEntity = new HttpEntity<Customer>(newCustomer, headers);
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                formFullURLWithPort(_add), HttpMethod.POST, httpEntity, String.class);

        String responseJson = responseEntity.getBody();
        assertEquals(customerJson, responseJson);

        //UNDO
        customerRepository.deleteById(newCustomer.getTckn());
    }

    @Test
    void addCustomer_TcknAlreadyExist_ThrowsDuplicateTcknException() throws JsonProcessingException {
        Customer newCustomer = new Customer("Dummy", "Test",10000000850L, "1234567890",1234D);

        String _add = "/customer/add";

        try {
            HttpEntity<Customer> httpEntity = new HttpEntity<Customer>(newCustomer, headers);
            ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                    formFullURLWithPort(_add), HttpMethod.POST, httpEntity, String.class);
        } catch (DuplicateTcknException e) {
            assertEquals("Provided TCKN already exists.\nCannot accept duplicate TCKN.\n", e.getMessage());
        }
    }

    @Test
    void updateCustomer() throws JsonProcessingException {
        //adding a customer first
        Customer newCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        String _add = "/customer/add";

        String addCustomerJson = this.mapToJson(newCustomer);

        HttpEntity<Customer> postHttpEntity = new HttpEntity<Customer>(newCustomer, headers);
        testRestTemplate.exchange(formFullURLWithPort(_add), HttpMethod.POST, postHttpEntity, String.class);

        Customer updatedCustomer = new Customer("Test", "Dummy",12345678910L, "0987654321",4321D);

        String _update = "/customer/update";

        String updateCustomerJson = this.mapToJson(updatedCustomer);

        HttpEntity<Customer> updateHttpEntity = new HttpEntity<Customer>(updatedCustomer, headers);
        ResponseEntity<String> updateResponseEntity = testRestTemplate.exchange(
                formFullURLWithPort(_update), HttpMethod.PUT, updateHttpEntity, String.class);

        String updateResponseJson = updateResponseEntity.getBody();
        assertEquals(updateCustomerJson, updateResponseJson);
        assertNotEquals(addCustomerJson, updateResponseJson);
    }

    @Test
    void updateCustomer_TcknDoesNotExist_ThrowsNotFoundException() throws Exception {
        String _update = "/customer/update";
        Customer updatedCustomer = new Customer("Test", "Dummy",12345678910L, "0987654321",4321D);

        try {
            HttpEntity<Customer> updateHttpEntity = new HttpEntity<Customer>(updatedCustomer, headers);
            ResponseEntity<String> updateResponseEntity = testRestTemplate.exchange(
                    formFullURLWithPort(_update), HttpMethod.PUT, updateHttpEntity, String.class);
        } catch (NotFoundException e){
            assertEquals("Customer tckn: 12345678910 not found!", e.getMessage());
        }
    }

    @Test
    void deleteCustomer() throws JsonProcessingException {
        Customer newCustomer = new Customer("Dummy", "Test",12345678910L, "1234567890",1234D);

        String _add = "/customer/add";

        String addCustomerJson = this.mapToJson(newCustomer);

        HttpEntity<Customer> postHttpEntity = new HttpEntity<Customer>(newCustomer, headers);
        testRestTemplate.exchange(formFullURLWithPort(_add), HttpMethod.POST, postHttpEntity, String.class);

        String _delete = "/customer/delete?tckn=12345678910";

        HttpEntity<Customer> deleteHttpEntity = new HttpEntity<Customer>(newCustomer, headers);
        ResponseEntity<String> deleteResponseEntity = testRestTemplate.exchange(formFullURLWithPort(_delete), HttpMethod.DELETE, deleteHttpEntity, String.class);

        assertEquals("Successfully deleted.", deleteResponseEntity.getBody());
        assertEquals(200, deleteResponseEntity.getStatusCode().value());
    }

    @Test
    void deleteCustomer_TcknDoesNotExist_ThrowsNotFoundException() throws Exception {
        String _delete = "/customer/delete";

        mockMvc.perform(delete(_delete).param("tckn", "12345678910"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("Delete operation is not successful. The customer does not exist.", result.getResolvedException().getMessage()));
    }

    @Test
    void applyLoan() throws Exception {
        Customer customer = new Customer("Thor","Parks",10000000950L,"8343458383",6676D);
        Map<Double, Boolean> expectedLoanMap = new HashMap<>();
        expectedLoanMap.put(26704D, true);

        String _applyLoan = "/customer/loan/apply";

        MockHttpServletResponse response = mockMvc.perform(get(_applyLoan).param("tckn", "10000000950")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json")).andReturn().getResponse();
        String responseContentAsString = response.getContentAsString();
        Map<Double, Boolean> actualLoanMap = new ObjectMapper().readValue(responseContentAsString, Map.class);

        assertTrue(actualLoanMap.containsValue(true));
        //TODO IMPROVEMENT: Compare doubles instead of strings. Currently skipped due to not being able to cast string to double from actualLoanMap.
        assertEquals("26704.0", actualLoanMap.keySet().stream().findFirst().get());

    }

    @Test
    void applyLoan_NotValidTckn_ThrowsIllegalTcknException() throws Exception {
        Customer customer = new Customer("Thor","Parks",10000000950L,"8343458383",6676D);

        String _applyLoan = "/customer/loan/apply";

        mockMvc.perform(get(_applyLoan).param("tckn", "1000000095"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalTcknException))
                .andExpect(result -> assertEquals("TCKN needs to be 11 digits and can only contain only numbers.", result.getResolvedException().getMessage()));
    }

    @Test
    void getLoans_ApprovedTrue() throws Exception {
        Customer customer = new Customer("Thor","Parks",10000000850L,"8343458383",6676D);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("tckn", 10000000850L);
        objectNode.put("approved", true);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        String requestJson = objectWriter.writeValueAsString(objectNode);

        String _loanHistory = "/customer/loan/history";

        MockHttpServletResponse response = mockMvc.perform(get(_loanHistory).contentType(MediaType.APPLICATION_JSON).content(requestJson)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        String responseContentAsString = response.getContentAsString();

        //TODO IMPROVEMENT: Parse response JSON to List<Loan> and validate it. Currently only comparing them as strings.
        String expectedLoansAsString = "[{\"loanAmount\":20000.0,\"approvalDate\":\"2021-03-08T21:38:39.161\",\"approvalStatus\":true},{\"loanAmount\":10000.0,\"approvalDate\":\"2022-02-12T20:52:08.164\",\"approvalStatus\":true}]";
        assertEquals(expectedLoansAsString, responseContentAsString);

        //List<Loan> responseList = objectMapper.readValue(responseContentAsString, new TypeReference<List<Loan>>() {});
    }

    @Test
    void getLoans_ApprovedFalse() throws Exception {
        Customer customer = new Customer("Ainsley","Hopper",10000000850L,"8794964085",5369D);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("tckn", 10000000850L);
        objectNode.put("approved", false);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        String requestJson = objectWriter.writeValueAsString(objectNode);

        String _loanHistory = "/customer/loan/history";

        MockHttpServletResponse response = mockMvc.perform(get(_loanHistory).contentType(MediaType.APPLICATION_JSON).content(requestJson)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        String responseContentAsString = response.getContentAsString();
        System.out.println(responseContentAsString);
        //TODO IMPROVEMENT: Parse response JSON to List<Loan> and validate it. Currently only comparing them as strings.
        String expectedLoansAsString = "[{\"loanAmount\":20000.0,\"approvalDate\":\"2021-03-08T21:38:39.161\",\"approvalStatus\":true},{\"loanAmount\":0.0,\"approvalDate\":\"2022-01-16T22:33:52.164\",\"approvalStatus\":false},{\"loanAmount\":10000.0,\"approvalDate\":\"2022-02-12T20:52:08.164\",\"approvalStatus\":true}]";
        assertEquals(expectedLoansAsString, responseContentAsString);
    }

    @Test
    void getLoans_RequestBodyDoesNotHaveTckn_ThrowsIllegalArgumentException() throws Exception {
        Customer customer = new Customer("Thor","Parks",10000000950L,"8343458383",6676D);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("approved", false);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        String requestJson = objectWriter.writeValueAsString(objectNode);

        String _loanHistory = "/customer/loan/history";

        mockMvc.perform(get(_loanHistory).contentType(MediaType.APPLICATION_JSON).content(requestJson)).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("Provided body is not valid.\nBody needs to have an 11 digits value for TCKN." +
                        "\nExample:\n{\n\"tckn\" : \"12345678910\",\n\"approved\" : true\"\n}", result.getResolvedException().getMessage()));
    }

    @Test
    void getLoans_NotValidTckn_ThrowsIllegalTcknException() throws Exception {
        Customer customer = new Customer("Thor","Parks",10000000950L,"8343458383",6676D);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("tckn", 124567891);
        objectNode.put("approved", false);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        String requestJson = objectWriter.writeValueAsString(objectNode);

        String _loanHistory = "/customer/loan/history";

        mockMvc.perform(get(_loanHistory).contentType(MediaType.APPLICATION_JSON).content(requestJson)).andDo(print()).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalTcknException))
                .andExpect(result -> assertEquals("TCKN needs to be 11 digits and can only contain only numbers.", result.getResolvedException().getMessage()));
    }
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        return b;
    }

    /**
     * This utility method Maps an Object to a JSON String. Uses a Jackson ObjectMapper.
     */
    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        return objectMapper.writeValueAsString(object);
    }

    /**
     * This utility method to create the url for given uri. It appends the RANDOM_PORT generated port
     */
    private String formFullURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    /**
     * This utility method is to build a LocalDateTime from String.
     * */
    private static LocalDateTime stringToDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.parse(dateString, formatter);
    }
    private static List<Customer> allCustomers = Arrays.asList(new Customer("Ainsley","Hopper",10000000850L,"8794964085",5369D),
                                                               new Customer("Thor","Parks",10000000950L,"8343458383",6676D),
                                                               new Customer("Merritt","Woods",10000000050L,"6154776828",6858D),
                                                               new Customer("Bruno","Avila",10000000810L,"3757713291",4934D),
                                                               new Customer("Sacha","Ashley",10000000910L,"8542845257",3975D),
                                                               new Customer("Hayes","Willis",10000000010L,"1724644137",4887D));
    private static List<Loan> loanListOf850 = Arrays.asList(new Loan(stringToDate("2021-03-08 21:38:39.161"),true,20000D, allCustomers.get(0)),
            new Loan(stringToDate("2022-01-16 22:33:52.164"),false,0D, allCustomers.get(0)),
            new Loan(stringToDate("2022-02-12 20:52:08.164"),true,10000d, allCustomers.get(0)));
}