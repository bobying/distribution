package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.PayTypeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity PayType and its DTO PayTypeDTO.
 */
@Mapper(componentModel = "spring", uses = {StatusMapper.class})
public interface PayTypeMapper extends EntityMapper<PayTypeDTO, PayType> {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    PayTypeDTO toDto(PayType payType); 

    @Mapping(source = "statusId", target = "status")
    PayType toEntity(PayTypeDTO payTypeDTO);

    default PayType fromId(Long id) {
        if (id == null) {
            return null;
        }
        PayType payType = new PayType();
        payType.setId(id);
        return payType;
    }
}
