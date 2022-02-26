package com.loanapp.loanapplication.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loanapp.loanapplication.exception.TcknValidator;
import com.loanapp.loanapplication.model.Customer;
import com.loanapp.loanapplication.model.Loan;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import com.loanapp.loanapplication.service.CustomerService;
import com.loanapp.loanapplication.service.LoanService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Validated
@RestController
@RequestMapping("/customer")
@Api(value = "Customer Controller")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoanService loanService;
    /**
     * @apiNote GetMapping for all customers.
     * @return Iterable<Customer>
     */
    @ApiOperation(value = "Get all customers")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Fetched customers successfully", response = Customer.class, responseContainer = "ResponseEntity")})
    @GetMapping("/all")
    public List<Customer> getAll(){
        List<Customer> customers = customerService.getAll();
        List<Loan> loanList;
        for (Customer customer : customers) {
            loanList = loanService.getApprovedLoansById(customer.getTckn());
            customer.setLoanList(loanList);
        }
        return customers;
    }
    /**
     * @apiNote GetMapping for Customer.
     * @param tckn As a request parameter.
     * @return ResponseEntity<Customer>
     */
    @ApiOperation(value = "Get customer for given TCKN")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Fetched customer successfully", response = Customer.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 404, message = "Customer not found.")})
    @GetMapping("/get")
    public ResponseEntity<Customer> getByTckn(@RequestParam(name = "tckn") @ApiParam(name = "tckn",
                                              type = "Long", required = true, example = "12345678910") Long tckn){
        TcknValidator.validate(tckn);
        List<Loan> loanList = loanService.getApprovedLoansById(tckn);
        Customer customer = customerService.getByTckn(tckn);
        customer.setLoanList(loanList);
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }
    /**
     * @apiNote Adds a Customer to database. Customer TCKN and phone number are validated.
     * @param customerDto as a request body.
     * @return A generic ResponseEntity<> - If adding customer is successful, status is OK, else BAD_REQUEST.
     */

//    @ApiImplicitParams({
//            @ApiImplicitParam(
//                    name = "customerDto",
//                    dataTypeClass = CustomerDto.class,
//                    examples = @Example(
//                            value = {
//                                    @ExampleProperty(value = "{'tckn' : 12345678910,\n" +
//                                                              "'name' : 'Emin',\n" +
//                                                              "'lastName' : 'Yilmaz',\n" +
//                                                              "'phoneNumber' : '1234567890'\n" +
//                                                              "'monthlySalary' : 1234}", mediaType = "application/json")}))})
    @PostMapping("/add")
    @ApiOperation(value = "Add a customer")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Customer added successfully", response = Customer.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Bad request. Duplicate TCKN.")})
    public ResponseEntity<Customer> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
        TcknValidator.validate(customerDto.getTckn());
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addCustomer(customerDto));
    }
    /**
     * @apiNote Updates an already existing Customer. Customer TCKN and phone number are validated.
     * @param customerDto as a request body.
     * @return ResponseEntity<Customer> - If updating the customer is successful, status is OK, else NOT_FOUND.
     */
    @ApiOperation(value = "Update an existing customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer updated successfully", response = Customer.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 404, message = "Not found. TCKN does not exist.")})
    @PutMapping("/update")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody CustomerDto customerDto) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(customerDto));
    }
    /**
     * @apiNote DeleteMapping for an existing Customer.
     * @param tckn as a request parameter.
     * @return A generic ResponseEntity<> - If to be deleted customer exists, returns an OK status. Else NOT_FOUND.
     */
    @ApiOperation(value = "Delete a customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer deleted successfully"),
            @ApiResponse(code = 400, message = "Not found. TCKN does not exist.")})
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCustomer(@RequestParam(name = "tckn") @ApiParam(name = "tckn", type = "Long",
                                            required = true, example = "12345678910") Long tckn) {
        customerService.deleteCustomer(tckn);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted.");
    }
    /**
     * @apiNote This API is to apply for loan. If the provided Customer does not in the customer table, it is saved. Else
     * does not update it and throws IllegalArgumentException.
     * @param tckn as a request parameter. Value is validated to be 11 digits number.
     * @return ResponseEntity<Map<Double, Boolean>> - Keys of the returned map is the loan amount. It is equal to 0D if it is declined.
     * Values of the returned map is th Boolean value of the approval status of the application.
     */
    @ApiOperation(value = "Apply loan with customer tckn")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Loan application is processed successfully", response = Map.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Bad request. TCKN is not valid."),
            @ApiResponse(code = 404, message = "Customer not found.")})
    @GetMapping ("/loan/apply")
    public ResponseEntity<Map<Double, Boolean>> applyLoan(@RequestParam(name = "tckn") @ApiParam(name = "tckn", type = "Long",
                                                          required = true, example = "12345678910") Long tckn) {
        TcknValidator.validate(tckn);
        return ResponseEntity.ok(loanService.applyLoan(tckn));
    }
    /**
     * @apiNote This API is to get the history of loan applications of a customer.
     * @param objectNode fields: tckn, approved (optional)
     * @return if approved field in the objectNode is true, returns only approved loans as a LoanDto list. Else, all of them.
     */
    @ApiOperation(value = "Fetch customer's loan history with TCKN")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Fetched loan history successfully", response = Loan.class, responseContainer = "ResponseEntity of List"),
            @ApiResponse(code = 400, message = "Bad request. TCKN is not valid."),
            @ApiResponse(code = 404, message = "Customer not found.")})
    @GetMapping("/loan/history")
    @ResponseBody
    public ResponseEntity<List<Loan>> getLoans (@RequestBody @ApiParam(name = "objectNode", example = "{\"tckn\" : 12345678910, \"approved\" : \"true\"}") ObjectNode objectNode) {
        return ResponseEntity.status(HttpStatus.OK).body(loanService.getLoans(objectNode));
    }
}

