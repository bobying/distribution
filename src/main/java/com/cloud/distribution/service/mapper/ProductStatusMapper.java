package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.ProductStatusDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ProductStatus and its DTO ProductStatusDTO.
 */
@Mapper(componentModel = "spring", uses = {StatusMapper.class})
public interface ProductStatusMapper extends EntityMapper<ProductStatusDTO, ProductStatus> {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    ProductStatusDTO toDto(ProductStatus productStatus); 

    @Mapping(source = "statusId", target = "status")
    ProductStatus toEntity(ProductStatusDTO productStatusDTO);

    default ProductStatus fromId(Long id) {
        if (id == null) {
            return null;
        }
        ProductStatus productStatus = new ProductStatus();
        productStatus.setId(id);
        return productStatus;
    }
}
