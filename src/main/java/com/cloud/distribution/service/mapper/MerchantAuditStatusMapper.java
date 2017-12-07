package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.MerchantAuditStatusDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity MerchantAuditStatus and its DTO MerchantAuditStatusDTO.
 */
@Mapper(componentModel = "spring", uses = {StatusMapper.class})
public interface MerchantAuditStatusMapper extends EntityMapper<MerchantAuditStatusDTO, MerchantAuditStatus> {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    MerchantAuditStatusDTO toDto(MerchantAuditStatus merchantAuditStatus); 

    @Mapping(source = "statusId", target = "status")
    MerchantAuditStatus toEntity(MerchantAuditStatusDTO merchantAuditStatusDTO);

    default MerchantAuditStatus fromId(Long id) {
        if (id == null) {
            return null;
        }
        MerchantAuditStatus merchantAuditStatus = new MerchantAuditStatus();
        merchantAuditStatus.setId(id);
        return merchantAuditStatus;
    }
}
