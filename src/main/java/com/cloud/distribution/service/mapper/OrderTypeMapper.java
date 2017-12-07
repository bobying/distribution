package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.OrderTypeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity OrderType and its DTO OrderTypeDTO.
 */
@Mapper(componentModel = "spring", uses = {StatusMapper.class})
public interface OrderTypeMapper extends EntityMapper<OrderTypeDTO, OrderType> {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    OrderTypeDTO toDto(OrderType orderType); 

    @Mapping(source = "statusId", target = "status")
    OrderType toEntity(OrderTypeDTO orderTypeDTO);

    default OrderType fromId(Long id) {
        if (id == null) {
            return null;
        }
        OrderType orderType = new OrderType();
        orderType.setId(id);
        return orderType;
    }
}
