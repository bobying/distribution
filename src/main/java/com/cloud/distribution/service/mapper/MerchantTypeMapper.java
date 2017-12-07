package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.MerchantTypeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity MerchantType and its DTO MerchantTypeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MerchantTypeMapper extends EntityMapper<MerchantTypeDTO, MerchantType> {

    

    

    default MerchantType fromId(Long id) {
        if (id == null) {
            return null;
        }
        MerchantType merchantType = new MerchantType();
        merchantType.setId(id);
        return merchantType;
    }
}
