package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.ProductTypeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ProductType and its DTO ProductTypeDTO.
 */
@Mapper(componentModel = "spring", uses = {StatusMapper.class})
public interface ProductTypeMapper extends EntityMapper<ProductTypeDTO, ProductType> {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    ProductTypeDTO toDto(ProductType productType); 

    @Mapping(source = "statusId", target = "status")
    ProductType toEntity(ProductTypeDTO productTypeDTO);

    default ProductType fromId(Long id) {
        if (id == null) {
            return null;
        }
        ProductType productType = new ProductType();
        productType.setId(id);
        return productType;
    }
}
