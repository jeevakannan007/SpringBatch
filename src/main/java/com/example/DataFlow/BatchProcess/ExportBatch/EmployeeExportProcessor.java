package com.example.DataFlow.BatchProcess.ExportBatch;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EmployeeExportProcessor
        implements ItemProcessor<EmployeeMVEntity, String> {

    private final Gson gson;

    public EmployeeExportProcessor() {

        this.gson = new GsonBuilder()
                .registerTypeAdapter(
                        LocalDate.class,
                        new LocalDateAdapter()
                )
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public String process(EmployeeMVEntity employee) throws Exception {

        return gson.toJson(employee);
    }
}