package com.cloud.distribution.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the Merchant entity. This class is used in MerchantResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /merchants?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class MerchantCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private LongFilter level;

    private LongFilter userId;

    private StringFilter address;

    private StringFilter mobile;

    private LongFilter merchantTypeId;

    private LongFilter merchantAuditStatusId;

    private LongFilter merchantStatusId;

    private LongFilter parentId;

    public MerchantCriteria() {
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

    public LongFilter getLevel() {
        return level;
    }

    public void setLevel(LongFilter level) {
        this.level = level;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public StringFilter getAddress() {
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getMobile() {
        return mobile;
    }

    public void setMobile(StringFilter mobile) {
        this.mobile = mobile;
    }

    public LongFilter getMerchantTypeId() {
        return merchantTypeId;
    }

    public void setMerchantTypeId(LongFilter merchantTypeId) {
        this.merchantTypeId = merchantTypeId;
    }

    public LongFilter getMerchantAuditStatusId() {
        return merchantAuditStatusId;
    }

    public void setMerchantAuditStatusId(LongFilter merchantAuditStatusId) {
        this.merchantAuditStatusId = merchantAuditStatusId;
    }

    public LongFilter getMerchantStatusId() {
        return merchantStatusId;
    }

    public void setMerchantStatusId(LongFilter merchantStatusId) {
        this.merchantStatusId = merchantStatusId;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "MerchantCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (level != null ? "level=" + level + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (address != null ? "address=" + address + ", " : "") +
                (mobile != null ? "mobile=" + mobile + ", " : "") +
                (merchantTypeId != null ? "merchantTypeId=" + merchantTypeId + ", " : "") +
                (merchantAuditStatusId != null ? "merchantAuditStatusId=" + merchantAuditStatusId + ", " : "") +
                (merchantStatusId != null ? "merchantStatusId=" + merchantStatusId + ", " : "") +
                (parentId != null ? "parentId=" + parentId + ", " : "") +
            "}";
    }

}
