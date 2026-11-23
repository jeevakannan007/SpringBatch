package com.example.DataFlow.EmployeeService;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.DataFlow.EmployeEntity.EmployeEntity;
import com.example.DataFlow.EmployeeRepositary.EmployeRepository;

@Component
public class EmployeeImportPersister {

    private final EmployeRepository repository;

    public EmployeeImportPersister(EmployeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveAll(List<EmployeEntity> entities) {
        repository.saveAll(entities);
    }
}