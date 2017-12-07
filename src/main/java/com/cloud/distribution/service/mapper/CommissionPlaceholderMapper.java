package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.CommissionPlaceholderDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity CommissionPlaceholder and its DTO CommissionPlaceholderDTO.
 */
@Mapper(componentModel = "spring", uses = {StatusMapper.class})
public interface CommissionPlaceholderMapper extends EntityMapper<CommissionPlaceholderDTO, CommissionPlaceholder> {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    CommissionPlaceholderDTO toDto(CommissionPlaceholder commissionPlaceholder); 

    @Mapping(source = "statusId", target = "status")
    CommissionPlaceholder toEntity(CommissionPlaceholderDTO commissionPlaceholderDTO);

    default CommissionPlaceholder fromId(Long id) {
        if (id == null) {
            return null;
        }
        CommissionPlaceholder commissionPlaceholder = new CommissionPlaceholder();
        commissionPlaceholder.setId(id);
        return commissionPlaceholder;
    }
}
