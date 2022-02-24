package com.loanapp.loanapplication.model.dto;

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
public class CustomerDto implements Serializable {
    @Digits(fraction = 0, integer = 11)
    @NotNull(message = "TCKN cannot be empty.")
    private Long tckn;
    private String name;
    private String lastName;
    @Pattern(regexp = "^[0-9]{10}", message = "Phone number needs to be 10 digits and can only contain only numbers.")
    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;
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
