package com.example.DataFlow.BatchProcess.importBatch;

import com.example.DataFlow.EmployeEntity.*;
import com.example.DataFlow.EmployeVO.EmployeVO;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor
        implements ItemProcessor<EmployeVO, EmployeEntity> {

    @Override
    public EmployeEntity process(EmployeVO vo) {

        EmployeEntity entity = new EmployeEntity();

        entity.setEmployeeCode(vo.getEmployeeCode());
        entity.setFirstName(vo.getFirstName());
        entity.setLastName(vo.getLastName());
        entity.setEmail(vo.getEmail());
        entity.setPhoneNumber(vo.getPhoneNumber());
        entity.setGender(vo.getGender());
        entity.setDateOfBirth(vo.getDateOfBirth());
        entity.setDepartment(vo.getDepartment());
        entity.setDesignation(vo.getDesignation());
        entity.setEmploymentType(vo.getEmploymentType());
        entity.setJoiningDate(vo.getJoiningDate());
        entity.setSalary(vo.getSalary());
        entity.setAddress(vo.getAddress());
        entity.setCity(vo.getCity());
        entity.setState(vo.getState());
        entity.setCountry(vo.getCountry());
        entity.setPinCode(vo.getPinCode());
        entity.setActive(vo.getActive());

        return entity;
    }

}