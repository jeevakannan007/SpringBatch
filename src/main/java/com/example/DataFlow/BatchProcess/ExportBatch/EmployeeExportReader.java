package com.example.DataFlow.BatchProcess.ExportBatch;

import java.text.ParseException;
import java.util.List;

import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.NonTransientResourceException;
import org.springframework.batch.infrastructure.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

public class EmployeeExportReader implements ItemReader<EmployeeMVEntity>  {


    @Autowired
    private EmployeeMVRepository employeeMVRepository;

    private List<EmployeeMVEntity> data;
    private int currentIndex = 0;
  
    public void SetEmployeeMVRepository(EmployeeMVRepository employeeMVRepository)
    {
        this.employeeMVRepository=employeeMVRepository;
    }

    public void resetReader() {
        this.data = null;
        this.currentIndex = 0;
    }


    @Override
    public EmployeeMVEntity read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (data == null) {
            // Load all data for the date range
            data = employeeMVRepository.findTop10Employees();
            currentIndex = 0;
        }

        if (currentIndex >= data.size()) {
            return null; // End of data
        }

        return data.get(currentIndex++);
    }

    public int getTotalRecords() {
        return data != null ? data.size() : 0;
    }
    
}