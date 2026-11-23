package com.example.DataFlow.EmployeVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeVO {

    private Long id;

    @NotBlank(message = "employeeCode is required")
    @Size(max = 20, message = "employeeCode must be at most 20 characters")
    private String employeeCode;

    @NotBlank(message = "firstName is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "phoneNumber is required")
    @Pattern(regexp = "^\\d{10}$", message = "phoneNumber must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "gender is required")
    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "gender must be MALE, FEMALE, or OTHER")
    private String gender;

    private LocalDate dateOfBirth;

    @NotBlank(message = "department is required")
    @Size(max = 50)
    private String department;

    @NotBlank(message = "designation is required")
    @Size(max = 50)
    private String designation;

    @NotBlank(message = "employmentType is required")
    @Pattern(regexp = "FULL_TIME|PART_TIME|CONTRACT|INTERN",
            message = "employmentType must be FULL_TIME, PART_TIME, CONTRACT, or INTERN")
    private String employmentType;

    private LocalDate joiningDate;

    @NotNull(message = "salary is required")
    @DecimalMin(value = "0.0", message = "salary must be zero or positive")
    private BigDecimal salary;

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String city;

    @Size(max = 50)
    private String state;

    @Size(max = 50)
    private String country;

    @Pattern(regexp = "^\\d{6}$|^$", message = "pinCode must be exactly 6 digits when provided")
    private String pinCode;

    @NotNull(message = "active is required")
    private Boolean active;

    private LocalDateTime createdAt;
}