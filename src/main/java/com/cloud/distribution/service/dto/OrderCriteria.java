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
 * Criteria class for the Order entity. This class is used in OrderResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /orders?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrderCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private StringFilter code;

    private LongFilter amount;

    private ZonedDateTimeFilter createdDate;

    private LongFilter userId;

    private LongFilter payTypeId;

    private LongFilter orderTypeId;

    private LongFilter orderStatusId;

    private LongFilter productId;

    public OrderCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getCode() {
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public LongFilter getAmount() {
        return amount;
    }

    public void setAmount(LongFilter amount) {
        this.amount = amount;
    }

    public ZonedDateTimeFilter getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTimeFilter createdDate) {
        this.createdDate = createdDate;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getPayTypeId() {
        return payTypeId;
    }

    public void setPayTypeId(LongFilter payTypeId) {
        this.payTypeId = payTypeId;
    }

    public LongFilter getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(LongFilter orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public LongFilter getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(LongFilter orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "OrderCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (code != null ? "code=" + code + ", " : "") +
                (amount != null ? "amount=" + amount + ", " : "") +
                (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (payTypeId != null ? "payTypeId=" + payTypeId + ", " : "") +
                (orderTypeId != null ? "orderTypeId=" + orderTypeId + ", " : "") +
                (orderStatusId != null ? "orderStatusId=" + orderStatusId + ", " : "") +
                (productId != null ? "productId=" + productId + ", " : "") +
            "}";
    }

}
