package com.example.DataFlow.BatchProcess.ExportBatch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmployeeExportJobConfig {

    private final EmployeeMVRepository employeeMVRepository;

    public EmployeeExportJobConfig(EmployeeMVRepository employeeMVRepository) {
        this.employeeMVRepository = employeeMVRepository;
    }

    // Reader
    @Bean
    public EmployeeExportReader employeeExportReader() {

        EmployeeExportReader reader = new EmployeeExportReader();

        reader.SetEmployeeMVRepository(employeeMVRepository);

        return reader;
    }

    // Processor
    @Bean
    public EmployeeExportProcessor employeeExportProcessor() {
        return new EmployeeExportProcessor();
    }

    // Writer
    @Bean
    public EmployeeExportWriter employeeExportWriter() {
        return new EmployeeExportWriter();
    }

    // Step
    @Bean
    public Step employeeStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {

        return new StepBuilder("employeeStep", jobRepository)
                .<EmployeeMVEntity, String>chunk(10)
                .transactionManager(transactionManager)
                .reader(employeeExportReader())
                .processor(employeeExportProcessor())
                .writer(employeeExportWriter())
                .build();
    }

    // Job
    @Bean
    public Job employeeExportJob(
            JobRepository jobRepository,
            Step employeeStep) {

        return new JobBuilder("employeeExportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(employeeStep)
                .build();
    }
}