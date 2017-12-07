package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.OrderStatusDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity OrderStatus and its DTO OrderStatusDTO.
 */
@Mapper(componentModel = "spring", uses = {StatusMapper.class})
public interface OrderStatusMapper extends EntityMapper<OrderStatusDTO, OrderStatus> {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    OrderStatusDTO toDto(OrderStatus orderStatus); 

    @Mapping(source = "statusId", target = "status")
    OrderStatus toEntity(OrderStatusDTO orderStatusDTO);

    default OrderStatus fromId(Long id) {
        if (id == null) {
            return null;
        }
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setId(id);
        return orderStatus;
    }
}
