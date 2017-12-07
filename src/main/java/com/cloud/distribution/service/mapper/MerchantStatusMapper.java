package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.MerchantStatusDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity MerchantStatus and its DTO MerchantStatusDTO.
 */
@Mapper(componentModel = "spring", uses = {StatusMapper.class})
public interface MerchantStatusMapper extends EntityMapper<MerchantStatusDTO, MerchantStatus> {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    MerchantStatusDTO toDto(MerchantStatus merchantStatus); 

    @Mapping(source = "statusId", target = "status")
    MerchantStatus toEntity(MerchantStatusDTO merchantStatusDTO);

    default MerchantStatus fromId(Long id) {
        if (id == null) {
            return null;
        }
        MerchantStatus merchantStatus = new MerchantStatus();
        merchantStatus.setId(id);
        return merchantStatus;
    }
}
