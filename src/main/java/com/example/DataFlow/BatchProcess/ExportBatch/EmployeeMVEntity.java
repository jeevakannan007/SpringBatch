package com.example.DataFlow.BatchProcess.ExportBatch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "employee_last_500")
@Data
public class EmployeeMVEntity {

    @Id
    private Long id;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String gender;

    private LocalDate dateOfBirth;

    private String department;

    private String designation;

    private String employmentType;

    private LocalDate joiningDate;

    private BigDecimal salary;

    private String address;

    private String city;

    private String state;

    private String country;

    private String pinCode;

    private Boolean active;

    private LocalDateTime createdAt;
}