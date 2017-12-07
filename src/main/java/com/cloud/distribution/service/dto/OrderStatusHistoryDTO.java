package com.cloud.distribution.service.dto;


import java.time.ZonedDateTime;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the OrderStatusHistory entity.
 */
public class OrderStatusHistoryDTO implements Serializable {

    private Long id;

    private ZonedDateTime modifiedTime;

    @Lob
    private String desc;

    private String operatorCode;

    private Long operatorTypeId;

    private String operatorTypeName;

    private Long orderId;

    private String orderName;

    private Long oldStatusId;

    private String oldStatusName;

    private Long newStatusId;

    private String newStatusName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(ZonedDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public Long getOperatorTypeId() {
        return operatorTypeId;
    }

    public void setOperatorTypeId(Long operatorTypeId) {
        this.operatorTypeId = operatorTypeId;
    }

    public String getOperatorTypeName() {
        return operatorTypeName;
    }

    public void setOperatorTypeName(String operatorTypeName) {
        this.operatorTypeName = operatorTypeName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public Long getOldStatusId() {
        return oldStatusId;
    }

    public void setOldStatusId(Long orderStatusId) {
        this.oldStatusId = orderStatusId;
    }

    public String getOldStatusName() {
        return oldStatusName;
    }

    public void setOldStatusName(String orderStatusName) {
        this.oldStatusName = orderStatusName;
    }

    public Long getNewStatusId() {
        return newStatusId;
    }

    public void setNewStatusId(Long orderStatusId) {
        this.newStatusId = orderStatusId;
    }

    public String getNewStatusName() {
        return newStatusName;
    }

    public void setNewStatusName(String orderStatusName) {
        this.newStatusName = orderStatusName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrderStatusHistoryDTO orderStatusHistoryDTO = (OrderStatusHistoryDTO) o;
        if(orderStatusHistoryDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), orderStatusHistoryDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OrderStatusHistoryDTO{" +
            "id=" + getId() +
            ", modifiedTime='" + getModifiedTime() + "'" +
            ", desc='" + getDesc() + "'" +
            ", operatorCode='" + getOperatorCode() + "'" +
            "}";
    }
}
