package com.example.DataFlow.EmployeeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;

import com.example.DataFlow.EmployeVO.EmployeVO;

public final class EmployeeCsvValidator {

    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\d{10}$");
    private static final Pattern PIN = Pattern.compile("^\\d{6}$");
    private static final Set<String> GENDERS = Set.of("MALE", "FEMALE", "OTHER");
    private static final Set<String> EMPLOYMENT_TYPES = Set.of(
            "FULL_TIME", "PART_TIME", "CONTRACT", "INTERN");

    private EmployeeCsvValidator() {
    }

    public static List<String> validateRecord(
            CSVRecord record,
            Set<String> seenEmployeeCodes,
            Set<String> seenEmails) {

        List<String> errors = new ArrayList<>();

        String employeeCode = trim(record, "employeeCode");
        String firstName = trim(record, "firstName");
        String lastName = trim(record, "lastName");
        String email = trim(record, "email");
        String phone = trim(record, "phoneNumber");
        String gender = trim(record, "gender");
        String dobRaw = trim(record, "dateOfBirth");
        String department = trim(record, "department");
        String designation = trim(record, "designation");
        String employmentType = trim(record, "employmentType");
        String joiningRaw = trim(record, "joiningDate");
        String salaryRaw = trim(record, "salary");
        String pinCode = trim(record, "pinCode");
        String activeRaw = trim(record, "active");

        require(errors, employeeCode, "employeeCode");
        require(errors, firstName, "firstName");
        require(errors, lastName, "lastName");
        require(errors, email, "email");
        require(errors, phone, "phoneNumber");
        require(errors, gender, "gender");
        require(errors, department, "department");
        require(errors, designation, "designation");
        require(errors, employmentType, "employmentType");
        require(errors, joiningRaw, "joiningDate");
        require(errors, salaryRaw, "salary");
        require(errors, activeRaw, "active");

        if (employeeCode != null) {
            if (employeeCode.length() > 20) {
                errors.add("employeeCode must be at most 20 characters");
            }
            if (!seenEmployeeCodes.add(employeeCode)) {
                errors.add("duplicate employeeCode in CSV: " + employeeCode);
            }
        }

        if (email != null) {
            if (!EMAIL.matcher(email).matches()) {
                errors.add("invalid email format");
            }
            if (!seenEmails.add(email.toLowerCase())) {
                errors.add("duplicate email in CSV: " + email);
            }
        }

        if (phone != null && !PHONE.matcher(phone).matches()) {
            errors.add("phoneNumber must be exactly 10 digits");
        }

        if (gender != null && !GENDERS.contains(gender.toUpperCase())) {
            errors.add("gender must be one of: MALE, FEMALE, OTHER");
        }

        if (employmentType != null
                && !EMPLOYMENT_TYPES.contains(employmentType.toUpperCase())) {
            errors.add("employmentType must be one of: FULL_TIME, PART_TIME, CONTRACT, INTERN");
        }

        parseDate(errors, dobRaw, "dateOfBirth", false);
        parseDate(errors, joiningRaw, "joiningDate", true);

        if (salaryRaw != null) {
            try {
                BigDecimal salary = new BigDecimal(salaryRaw);
                if (salary.compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("salary must be zero or positive");
                }
            } catch (NumberFormatException e) {
                errors.add("salary must be a valid number");
            }
        }

        if (pinCode != null && !pinCode.isBlank() && !PIN.matcher(pinCode).matches()) {
            errors.add("pinCode must be exactly 6 digits when provided");
        }

        if (activeRaw != null
                && !"true".equalsIgnoreCase(activeRaw)
                && !"false".equalsIgnoreCase(activeRaw)) {
            errors.add("active must be true or false");
        }

        maxLength(errors, firstName, "firstName", 50);
        maxLength(errors, lastName, "lastName", 50);
        maxLength(errors, department, "department", 50);
        maxLength(errors, designation, "designation", 50);
        maxLength(errors, trim(record, "address"), "address", 255);
        maxLength(errors, trim(record, "city"), "city", 50);
        maxLength(errors, trim(record, "state"), "state", 50);
        maxLength(errors, trim(record, "country"), "country", 50);

        return errors;
    }

    public static EmployeVO toVo(CSVRecord record) {
        EmployeVO vo = new EmployeVO();
        vo.setEmployeeCode(trim(record, "employeeCode"));
        vo.setFirstName(trim(record, "firstName"));
        vo.setLastName(trim(record, "lastName"));
        vo.setEmail(trim(record, "email"));
        vo.setPhoneNumber(trim(record, "phoneNumber"));
        vo.setGender(trim(record, "gender").toUpperCase());
        vo.setDateOfBirth(parseDateValue(trim(record, "dateOfBirth")));
        vo.setDepartment(trim(record, "department"));
        vo.setDesignation(trim(record, "designation"));
        vo.setEmploymentType(trim(record, "employmentType").toUpperCase());
        vo.setJoiningDate(parseDateValue(trim(record, "joiningDate")));
        vo.setSalary(new BigDecimal(trim(record, "salary")));
        vo.setAddress(trim(record, "address"));
        vo.setCity(trim(record, "city"));
        vo.setState(trim(record, "state"));
        vo.setCountry(trim(record, "country"));
        vo.setPinCode(trim(record, "pinCode"));
        vo.setActive(Boolean.parseBoolean(trim(record, "active")));
        return vo;
    }

  public static final String[] REQUIRED_HEADERS = {
            "employeeCode", "firstName", "lastName", "email", "phoneNumber",
            "gender", "dateOfBirth", "department", "designation", "employmentType",
            "joiningDate", "salary", "address", "city", "state", "country",
            "pinCode", "active"
    };

    private static void require(List<String> errors, String value, String field) {
        if (value == null || value.isBlank()) {
            errors.add(field + " is required");
        }
    }

    private static void maxLength(List<String> errors, String value, String field, int max) {
        if (value != null && value.length() > max) {
            errors.add(field + " must be at most " + max + " characters");
        }
    }

    private static void parseDate(
            List<String> errors,
            String raw,
            String field,
            boolean required) {

        if (raw == null || raw.isBlank()) {
            if (required) {
                errors.add(field + " is required");
            }
            return;
        }
        try {
            LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            errors.add(field + " must be ISO date (yyyy-MM-dd)");
        }
    }

    private static LocalDate parseDateValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private static String trim(CSVRecord record, String column) {
        try {
            String value = record.get(column);
            return value == null ? null : value.trim();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}