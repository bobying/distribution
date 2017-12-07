package com.cloud.distribution.service.mapper;

import com.cloud.distribution.domain.*;
import com.cloud.distribution.service.dto.OrderStatusHistoryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity OrderStatusHistory and its DTO OrderStatusHistoryDTO.
 */
@Mapper(componentModel = "spring", uses = {OperatorTypeMapper.class, OrderMapper.class, OrderStatusMapper.class})
public interface OrderStatusHistoryMapper extends EntityMapper<OrderStatusHistoryDTO, OrderStatusHistory> {

    @Mapping(source = "operatorType.id", target = "operatorTypeId")
    @Mapping(source = "operatorType.name", target = "operatorTypeName")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.name", target = "orderName")
    @Mapping(source = "oldStatus.id", target = "oldStatusId")
    @Mapping(source = "oldStatus.name", target = "oldStatusName")
    @Mapping(source = "newStatus.id", target = "newStatusId")
    @Mapping(source = "newStatus.name", target = "newStatusName")
    OrderStatusHistoryDTO toDto(OrderStatusHistory orderStatusHistory); 

    @Mapping(source = "operatorTypeId", target = "operatorType")
    @Mapping(source = "orderId", target = "order")
    @Mapping(source = "oldStatusId", target = "oldStatus")
    @Mapping(source = "newStatusId", target = "newStatus")
    OrderStatusHistory toEntity(OrderStatusHistoryDTO orderStatusHistoryDTO);

    default OrderStatusHistory fromId(Long id) {
        if (id == null) {
            return null;
        }
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setId(id);
        return orderStatusHistory;
    }
}
