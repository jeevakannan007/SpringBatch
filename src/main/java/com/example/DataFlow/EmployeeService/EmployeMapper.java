package com.example.DataFlow.EmployeeService;

import org.mapstruct.Mapper;

import com.example.DataFlow.EmployeEntity.EmployeEntity;
import com.example.DataFlow.EmployeVO.EmployeVO;

@Mapper(componentModel = "spring")
public interface EmployeMapper {

    EmployeVO toVO(EmployeEntity entity);

    EmployeEntity toEntity(EmployeVO vo);
}