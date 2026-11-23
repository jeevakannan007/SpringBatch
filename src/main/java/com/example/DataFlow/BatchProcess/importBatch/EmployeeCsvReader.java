package com.example.DataFlow.BatchProcess.importBatch;

import com.example.DataFlow.EmployeVO.EmployeVO;

import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class EmployeeCsvReader {

    @Bean
    public FlatFileItemReader<EmployeVO> employeeItemReader(
            FieldSetMapper<EmployeVO> employeeFieldSetMapper) {

        DefaultLineMapper<EmployeVO> lineMapper =

        new DefaultLineMapper<>();

        FlatFileItemReader<EmployeVO> reader =
                new FlatFileItemReader<>(lineMapper);

        reader.setResource(
                new ClassPathResource("file/employees_sample.csv")
        );

        reader.setLinesToSkip(1);

        DelimitedLineTokenizer tokenizer =
                new DelimitedLineTokenizer();

        tokenizer.setNames(
                "employeeCode",
                "firstName",
                "lastName",
                "email",
                "phoneNumber",
                "gender",
                "dateOfBirth",
                "department",
                "designation",
                "employmentType",
                "joiningDate",
                "salary",
                "address",
                "city",
                "state",
                "country",
                "pinCode",
                "active"
        );

        lineMapper.setLineTokenizer(tokenizer);

        // Inject the custom mapper
        lineMapper.setFieldSetMapper(employeeFieldSetMapper);

        return reader;
    }
}