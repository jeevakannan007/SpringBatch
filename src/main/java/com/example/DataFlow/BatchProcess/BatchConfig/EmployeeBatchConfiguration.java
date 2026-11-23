package com.example.DataFlow.BatchProcess.BatchConfig;

import java.time.LocalDate;

import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;

import com.example.DataFlow.EmployeVO.EmployeVO;

@Configuration
public class EmployeeBatchConfiguration {

    @Bean
public FieldSetMapper<EmployeVO> employeeFieldSetMapper() {

    BeanWrapperFieldSetMapper<EmployeVO> mapper =
            new BeanWrapperFieldSetMapper<>();

    mapper.setTargetType(EmployeVO.class);

    DefaultConversionService conversionService =
            new DefaultConversionService();

    conversionService.addConverter(
            String.class,
            LocalDate.class,
            source -> LocalDate.parse(source)
    );

    mapper.setConversionService(conversionService);

    return mapper;
}

}