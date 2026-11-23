package com.example.DataFlow.BatchProcess.ExportBatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import com.example.DataFlow.EmployeVO.EmployeVO;

public class EmployeeExportWriter implements ItemWriter<String> {

    private final List<String> employeeData = new ArrayList<>();

    @Override
    public void write(Chunk<? extends String> chunk) {

        employeeData.addAll(chunk.getItems());
    }

    public List<String> getEmployeeData() {
        return employeeData;
    }
    public void clearEmployeeData() {
    employeeData.clear();
}
}


