package com.example.DataFlow.BatchProcess.ExportBatch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.DataFlow.BatchProcess.*;;

@Repository
public interface EmployeeMVRepository extends JpaRepository<EmployeeMVEntity, Long> {


     @Query(value = "SELECT * FROM employee_last_500 LIMIT 10", nativeQuery = true)
    List<EmployeeMVEntity> findTop10Employees();
}