package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.MerchantDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Merchant and its DTO MerchantDTO.
 */
@Mapper(componentModel = "spring", uses = {MerchantTypeMapper.class, MerchantAuditStatusMapper.class, MerchantStatusMapper.class})
public interface MerchantMapper extends EntityMapper<MerchantDTO, Merchant> {

    @Mapping(source = "merchantType.id", target = "merchantTypeId")
    @Mapping(source = "merchantType.name", target = "merchantTypeName")
    @Mapping(source = "merchantAuditStatus.id", target = "merchantAuditStatusId")
    @Mapping(source = "merchantAuditStatus.name", target = "merchantAuditStatusName")
    @Mapping(source = "merchantStatus.id", target = "merchantStatusId")
    @Mapping(source = "merchantStatus.name", target = "merchantStatusName")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.name", target = "parentName")
    MerchantDTO toDto(Merchant merchant); 

    @Mapping(source = "merchantTypeId", target = "merchantType")
    @Mapping(source = "merchantAuditStatusId", target = "merchantAuditStatus")
    @Mapping(source = "merchantStatusId", target = "merchantStatus")
    @Mapping(source = "parentId", target = "parent")
    Merchant toEntity(MerchantDTO merchantDTO);

    default Merchant fromId(Long id) {
        if (id == null) {
            return null;
        }
        Merchant merchant = new Merchant();
        merchant.setId(id);
        return merchant;
    }
}
