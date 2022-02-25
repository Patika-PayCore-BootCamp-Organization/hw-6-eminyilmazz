package com.loanapp.loanapplication.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ApiModel(value = "Customer DTO")
public class CustomerDto implements Serializable {
    @Digits(fraction = 0, integer = 11)
    @NotNull(message = "TCKN cannot be empty.")
    @ApiModelProperty(name = "Customer TCKN", required = true, notes = "MUST be 11 digits number", example = "12345678910")
    private Long tckn;
    @ApiModelProperty(name = "Customer Name", required = true, example = "'Emin'")
    private String name;
    @ApiModelProperty(name = "Customer Last Name", required = true, example = "'Yilmaz'")
    private String lastName;
    @Pattern(regexp = "^[0-9]{10}", message = "Phone number needs to be 10 digits and can only contain only numbers.")
    @NotBlank(message = "Phone number cannot be empty")
    @ApiModelProperty(name = "Phone Number", required = true, notes = "MUST be 10 digits string", example = "'1234567890'")
    private String phoneNumber;
    @ApiModelProperty(name = "Monthly Salary", required = true, example = "1234")
    private Double monthlySalary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDto that = (CustomerDto) o;
        return getTckn().equals(that.getTckn()) && getName().equals(that.getName()) && getLastName().equals(that.getLastName()) && getPhoneNumber().equals(that.getPhoneNumber()) && getMonthlySalary().equals(that.getMonthlySalary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTckn(), getName(), getLastName(), getPhoneNumber(), getMonthlySalary());
    }
}
