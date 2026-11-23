package com.example.DataFlow.EmployeeRepositary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DataFlow.EmployeEntity.EmployeEntity;

@Repository
public interface EmployeRepository
        extends JpaRepository<EmployeEntity, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmailIgnoreCase(String email);
}