package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.OrderDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Order and its DTO OrderDTO.
 */
@Mapper(componentModel = "spring", uses = {PayTypeMapper.class, OrderTypeMapper.class, OrderStatusMapper.class, ProductMapper.class})
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {

    @Mapping(source = "payType.id", target = "payTypeId")
    @Mapping(source = "payType.name", target = "payTypeName")
    @Mapping(source = "orderType.id", target = "orderTypeId")
    @Mapping(source = "orderType.name", target = "orderTypeName")
    @Mapping(source = "orderStatus.id", target = "orderStatusId")
    @Mapping(source = "orderStatus.name", target = "orderStatusName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderDTO toDto(Order order); 

    @Mapping(source = "payTypeId", target = "payType")
    @Mapping(source = "orderTypeId", target = "orderType")
    @Mapping(source = "orderStatusId", target = "orderStatus")
    @Mapping(source = "productId", target = "product")
    Order toEntity(OrderDTO orderDTO);

    default Order fromId(Long id) {
        if (id == null) {
            return null;
        }
        Order order = new Order();
        order.setId(id);
        return order;
    }
}
