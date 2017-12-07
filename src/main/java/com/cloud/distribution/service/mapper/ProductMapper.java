package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.ProductDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Product and its DTO ProductDTO.
 */
@Mapper(componentModel = "spring", uses = {CurrencyMapper.class, ProductStatusMapper.class, ProductTypeMapper.class})
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {

    @Mapping(source = "currencyType.id", target = "currencyTypeId")
    @Mapping(source = "currencyType.name", target = "currencyTypeName")
    @Mapping(source = "productStatus.id", target = "productStatusId")
    @Mapping(source = "productStatus.name", target = "productStatusName")
    @Mapping(source = "productType.id", target = "productTypeId")
    @Mapping(source = "productType.name", target = "productTypeName")
    ProductDTO toDto(Product product); 

    @Mapping(source = "currencyTypeId", target = "currencyType")
    @Mapping(source = "productStatusId", target = "productStatus")
    @Mapping(source = "productTypeId", target = "productType")
    Product toEntity(ProductDTO productDTO);

    default Product fromId(Long id) {
        if (id == null) {
            return null;
        }
        Product product = new Product();
        product.setId(id);
        return product;
    }
}
