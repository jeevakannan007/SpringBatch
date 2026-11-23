package com.example.DataFlow.EmployeVO;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CsvImportResult {

    private int totalRows;
    private int importedCount;
    private int failedCount;
    private List<CsvRowError> errors = new ArrayList<>();

    @Data
    public static class CsvRowError {
        private final long rowNumber;
        private final String employeeCode;
        private final String message;
    }
}