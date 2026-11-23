package com.example.DataFlow.EmployeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employee")
@Data
public class EmployeEntity {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", length = 20, unique = true)
    private String employeeCode;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(length = 100, unique = true)
    private String email;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(length = 10)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 50)
    private String department;

    @Column(length = 50)
    private String designation;

    @Column(name = "employment_type", length = 30)
    private String employmentType;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(precision = 12, scale = 2)
    private java.math.BigDecimal salary;

    @Column(length = 255)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 50)
    private String country;

    @Column(name = "pin_code", length = 6)
    private String pinCode;

    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}