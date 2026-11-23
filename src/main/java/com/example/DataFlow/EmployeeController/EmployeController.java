package com.example.DataFlow.EmployeeController;

import java.util.List;

import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.DataFlow.EmployeeService.EmployeService;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.example.DataFlow.BatchProcess.ExportBatch.EmployeeExportReader;
import com.example.DataFlow.BatchProcess.ExportBatch.EmployeeExportWriter;
import com.example.DataFlow.EmployeVO.CsvImportResult;
import com.example.DataFlow.EmployeVO.EmployeVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeController {

    private final EmployeService employeService;
    private final Job employeeImportJob;
    private final JobOperator jobOperator;
    private final Job employeeExportJob;

    @Autowired 
    EmployeeExportWriter employeeExportWriter;
    @Autowired 
    EmployeeExportReader employeeExportReader;
    public EmployeController(EmployeService employeService,Job employeeImportJob, Job employeeExportJob,JobOperator jobOperator) {
        this.employeService = employeService;
        this.employeeImportJob=employeeImportJob;
        this.jobOperator =jobOperator;
        this.employeeExportJob=employeeExportJob;
    }



    @GetMapping("/batch/export")
public ResponseEntity<String> getExportData() throws Exception {

    // Reset the previous execution's data
    employeeExportWriter.clearEmployeeData();
    employeeExportReader.resetReader();

    JobParameters jobParameters = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

    // Execute EXPORT job, not IMPORT job
    var jobExecution = jobOperator.start(
            employeeExportJob,
            jobParameters
    );

    if (jobExecution.getStatus() != BatchStatus.COMPLETED) {

        return ResponseEntity.internalServerError()
                .body("{\"message\":\"Employee export job failed\"}");
    }

    List<String> data = employeeExportWriter.getEmployeeData();

    // Convert individual JSON strings into a proper JSON array
    JsonArray jsonArray = new JsonArray();

    for (String employee : data) {
        jsonArray.add(JsonParser.parseString(employee));
    }

    String jsonData = jsonArray.toString();

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(jsonData);
}

    @PostMapping("/batch/import")
    public String startImport() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestedBy", "API")
                .addLong("requestTime", System.currentTimeMillis())
                .toJobParameters();

        var jobExecution = jobOperator.start(
                employeeImportJob,
                jobParameters
        );

        return "Job started. Execution ID: "
                + jobExecution.getId()
                + ", Status: "
                + jobExecution.getStatus();
    }

    @PostMapping
    public ResponseEntity<EmployeVO> saveEmployee(@Valid @RequestBody EmployeVO employeeVO) {
        EmployeVO savedEmployee = employeService.saveEmployee(employeeVO);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @PostMapping(value = "/import-csv", consumes = "multipart/form-data")
    public ResponseEntity<CsvImportResult> importEmployees(
            @RequestParam("file") MultipartFile file) {

        CsvImportResult result = employeService.importEmployees(file);
        HttpStatus status = result.getImportedCount() > 0
                ? HttpStatus.CREATED
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(result);
    }

    @GetMapping
    public ResponseEntity<List<EmployeVO>> getAllEmployees() {
        return ResponseEntity.ok(employeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeVO> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeVO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeVO employeeVO) {
        return ResponseEntity.ok(employeService.updateEmployee(id, employeeVO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}