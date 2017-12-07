package com.cloud.distribution.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;



import io.github.jhipster.service.filter.ZonedDateTimeFilter;


/**
 * Criteria class for the OrderStatusHistory entity. This class is used in OrderStatusHistoryResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /order-status-histories?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrderStatusHistoryCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private ZonedDateTimeFilter modifiedTime;

    private StringFilter operatorCode;

    private LongFilter operatorTypeId;

    private LongFilter orderId;

    private LongFilter oldStatusId;

    private LongFilter newStatusId;

    public OrderStatusHistoryCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ZonedDateTimeFilter getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(ZonedDateTimeFilter modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public StringFilter getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(StringFilter operatorCode) {
        this.operatorCode = operatorCode;
    }

    public LongFilter getOperatorTypeId() {
        return operatorTypeId;
    }

    public void setOperatorTypeId(LongFilter operatorTypeId) {
        this.operatorTypeId = operatorTypeId;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
    }

    public LongFilter getOldStatusId() {
        return oldStatusId;
    }

    public void setOldStatusId(LongFilter oldStatusId) {
        this.oldStatusId = oldStatusId;
    }

    public LongFilter getNewStatusId() {
        return newStatusId;
    }

    public void setNewStatusId(LongFilter newStatusId) {
        this.newStatusId = newStatusId;
    }

    @Override
    public String toString() {
        return "OrderStatusHistoryCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (modifiedTime != null ? "modifiedTime=" + modifiedTime + ", " : "") +
                (operatorCode != null ? "operatorCode=" + operatorCode + ", " : "") +
                (operatorTypeId != null ? "operatorTypeId=" + operatorTypeId + ", " : "") +
                (orderId != null ? "orderId=" + orderId + ", " : "") +
                (oldStatusId != null ? "oldStatusId=" + oldStatusId + ", " : "") +
                (newStatusId != null ? "newStatusId=" + newStatusId + ", " : "") +
            "}";
    }

}
