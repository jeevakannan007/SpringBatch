package com.example.DataFlow.EmployeeService.imple;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.DataFlow.EmployeEntity.EmployeEntity;
import com.example.DataFlow.EmployeVO.CsvImportResult;
import com.example.DataFlow.EmployeVO.EmployeVO;
import com.example.DataFlow.EmployeeRepositary.EmployeRepository;
import com.example.DataFlow.EmployeeService.EmployeeCsvValidator;
import com.example.DataFlow.EmployeeService.EmployeeImportPersister;
import com.example.DataFlow.EmployeeService.EmployeMapper;
import com.example.DataFlow.EmployeeService.EmployeService;
import com.example.DataFlow.exception.ResourceNotFoundException;

@Service
@Transactional
public class EmployeServiceImpl implements EmployeService {

    private final EmployeRepository repository;
    private final EmployeMapper mapper;
    private final EmployeeImportPersister importPersister;
    private static final int BATCH_SIZE = 500;

    public EmployeServiceImpl(
            EmployeRepository repository,
            EmployeMapper mapper,
            EmployeeImportPersister importPersister) {
        this.repository = repository;
        this.mapper = mapper;
        this.importPersister = importPersister;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CsvImportResult importEmployees(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a CSV file");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are allowed");
        }

        CsvImportResult result = new CsvImportResult();
List<EmployeEntity> toSave = new ArrayList<>(BATCH_SIZE);
        Set<String> seenCodes = new HashSet<>();
        Set<String> seenEmails = new HashSet<>();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        try (
                Reader reader = new InputStreamReader(
                        file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, format)
        ) {
            validateHeaders(parser.getHeaderMap().keySet());

            for (CSVRecord record : parser) {

    long rowNumber = record.getRecordNumber() + 1;

    result.setTotalRows(result.getTotalRows() + 1);

    List<String> rowErrors = new ArrayList<>(
        EmployeeCsvValidator.validateRecord(record, seenCodes, seenEmails)
    );

    String code = safeGet(record, "employeeCode");
    String email = safeGet(record, "email");

    if (code != null && repository.existsByEmployeeCode(code)) {
        rowErrors.add("Employee code already exists: " + code);
    }

    if (email != null && repository.existsByEmailIgnoreCase(email)) {
        rowErrors.add("Email already exists: " + email);
    }

    if (!rowErrors.isEmpty()) {

        result.setFailedCount(result.getFailedCount() + 1);

        result.getErrors().add(
            new CsvImportResult.CsvRowError(
                rowNumber,
                code,
                String.join("; ", rowErrors)
            )
        );

        continue;
    }

    EmployeVO vo = EmployeeCsvValidator.toVo(record);

    EmployeEntity entity = mapper.toEntity(vo);

    entity.setCreatedAt(LocalDateTime.now());

    // Add employee to temporary list
    toSave.add(entity);

    // Check whether 500 records are collected
    if (toSave.size() == BATCH_SIZE) {

        System.out.println("Saving Batch: 500 records");

        importPersister.saveAll(new ArrayList<>(toSave));

        result.setImportedCount(
            result.getImportedCount() + toSave.size()
        );

        // Empty list for next batch
        toSave.clear();
    }
}

// Save remaining records
if (!toSave.isEmpty()) {

    System.out.println("Saving remaining records: " + toSave.size());

    importPersister.saveAll(new ArrayList<>(toSave));

    result.setImportedCount(
        result.getImportedCount() + toSave.size()
    );
}

           

            return result;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Employee CSV import failed: " + e.getMessage(), e);
        }
    }

    private void validateHeaders(Set<String> headers) {
        List<String> missing = Arrays.stream(EmployeeCsvValidator.REQUIRED_HEADERS)
                .filter(h -> !headers.contains(h))
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "CSV missing required columns: " + String.join(", ", missing));
        }
    }

    private String safeGet(CSVRecord record, String column) {
        try {
            return record.get(column);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // SAVE
    @Override
    public EmployeVO saveEmployee(EmployeVO employeeVO) {
     
        EmployeEntity entity = mapper.toEntity(employeeVO);
        entity.setCreatedAt(LocalDateTime.now());
        return mapper.toVO(repository.save(entity));
    }

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<EmployeVO> getAllEmployees() {
        return repository.findAll()
                .stream()
                .map(mapper::toVO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public EmployeVO getEmployeeById(Long id) {
        EmployeEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        return mapper.toVO(entity);
    }

    // UPDATE
    @Override
    public EmployeVO updateEmployee(Long id, EmployeVO employeeVO) {
        EmployeEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));

        if (!existing.getEmployeeCode().equals(employeeVO.getEmployeeCode())
                && repository.existsByEmployeeCode(employeeVO.getEmployeeCode())) {
            throw new IllegalArgumentException(
                    "employeeCode already exists: " + employeeVO.getEmployeeCode());
        }
        if (!existing.getEmail().equalsIgnoreCase(employeeVO.getEmail())
                && repository.existsByEmailIgnoreCase(employeeVO.getEmail())) {
            throw new IllegalArgumentException(
                    "email already exists: " + employeeVO.getEmail());
        }

        existing.setEmployeeCode(employeeVO.getEmployeeCode());
        existing.setFirstName(employeeVO.getFirstName());
        existing.setLastName(employeeVO.getLastName());
        existing.setEmail(employeeVO.getEmail());
        existing.setPhoneNumber(employeeVO.getPhoneNumber());
        existing.setGender(employeeVO.getGender());
        existing.setDateOfBirth(employeeVO.getDateOfBirth());
        existing.setDepartment(employeeVO.getDepartment());
        existing.setDesignation(employeeVO.getDesignation());
        existing.setEmploymentType(employeeVO.getEmploymentType());
        existing.setJoiningDate(employeeVO.getJoiningDate());
        existing.setSalary(employeeVO.getSalary());
        existing.setAddress(employeeVO.getAddress());
        existing.setCity(employeeVO.getCity());
        existing.setState(employeeVO.getState());
        existing.setCountry(employeeVO.getCountry());
        existing.setPinCode(employeeVO.getPinCode());
        existing.setActive(employeeVO.getActive());

        return mapper.toVO(repository.save(existing));
    }

    // DELETE
    @Override
    public void deleteEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found: " + id);
        }
        repository.deleteById(id);
    }
}