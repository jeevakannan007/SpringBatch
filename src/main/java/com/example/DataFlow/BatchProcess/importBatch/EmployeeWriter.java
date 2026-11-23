package com.example.DataFlow.BatchProcess.importBatch;

import com.example.DataFlow.EmployeEntity.EmployeEntity;
import com.example.DataFlow.EmployeVO.EmployeVO;
import com.example.DataFlow.EmployeeRepositary.EmployeRepository;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class EmployeeWriter implements ItemWriter<EmployeEntity> {

    private final EmployeRepository employeeRepository;

    public EmployeeWriter(EmployeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void write(Chunk<? extends EmployeEntity> chunk) {

        employeeRepository.saveAll(chunk.getItems());

        System.out.println("Employees saved: " + chunk.size());
    }
}