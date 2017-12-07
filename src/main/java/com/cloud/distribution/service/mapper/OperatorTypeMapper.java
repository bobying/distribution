package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.OperatorTypeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity OperatorType and its DTO OperatorTypeDTO.
 */
@Mapper(componentModel = "spring", uses = {StatusMapper.class})
public interface OperatorTypeMapper extends EntityMapper<OperatorTypeDTO, OperatorType> {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    OperatorTypeDTO toDto(OperatorType operatorType); 

    @Mapping(source = "statusId", target = "status")
    OperatorType toEntity(OperatorTypeDTO operatorTypeDTO);

    default OperatorType fromId(Long id) {
        if (id == null) {
            return null;
        }
        OperatorType operatorType = new OperatorType();
        operatorType.setId(id);
        return operatorType;
    }
}
