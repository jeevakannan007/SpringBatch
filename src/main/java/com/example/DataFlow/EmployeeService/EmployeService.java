package com.example.DataFlow.EmployeeService;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.DataFlow.EmployeVO.CsvImportResult;
import com.example.DataFlow.EmployeVO.EmployeVO;

public interface EmployeService {

    EmployeVO saveEmployee(EmployeVO employeeVO);

    List<EmployeVO> getAllEmployees();

    EmployeVO getEmployeeById(Long id);

    EmployeVO updateEmployee(Long id, EmployeVO employeeVO);

    void deleteEmployee(Long id);

    CsvImportResult importEmployees(MultipartFile file);
}