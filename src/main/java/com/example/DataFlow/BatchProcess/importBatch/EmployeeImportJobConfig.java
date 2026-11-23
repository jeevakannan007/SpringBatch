package com.example.DataFlow.BatchProcess.importBatch;

import com.example.DataFlow.EmployeEntity.EmployeEntity;
import com.example.DataFlow.EmployeVO.EmployeVO;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmployeeImportJobConfig {

    @Bean
    public Step employeeImportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<EmployeVO> employeeItemReader,
            ItemProcessor<EmployeVO, EmployeEntity> employeeProcessor,
            ItemWriter<EmployeEntity> employeeWriter) {

        return new StepBuilder("employeeImportStep", jobRepository)
                .<EmployeVO, EmployeEntity>chunk(500)
                .reader(employeeItemReader)
                .processor(employeeProcessor)
                .writer(employeeWriter)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Job employeeImportJob(
            JobRepository jobRepository,
            Step employeeImportStep) {

        return new JobBuilder("employeeImportJob", jobRepository)
                .start(employeeImportStep)
                .build();
    }
}