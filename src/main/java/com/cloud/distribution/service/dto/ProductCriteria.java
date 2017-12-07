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
 * Criteria class for the Product entity. This class is used in ProductResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /products?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private LongFilter price;

    private LongFilter remains;

    private LongFilter currencyTypeId;

    private LongFilter productStatusId;

    private LongFilter productTypeId;

    public ProductCriteria() {
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

    public LongFilter getPrice() {
        return price;
    }

    public void setPrice(LongFilter price) {
        this.price = price;
    }

    public LongFilter getRemains() {
        return remains;
    }

    public void setRemains(LongFilter remains) {
        this.remains = remains;
    }

    public LongFilter getCurrencyTypeId() {
        return currencyTypeId;
    }

    public void setCurrencyTypeId(LongFilter currencyTypeId) {
        this.currencyTypeId = currencyTypeId;
    }

    public LongFilter getProductStatusId() {
        return productStatusId;
    }

    public void setProductStatusId(LongFilter productStatusId) {
        this.productStatusId = productStatusId;
    }

    public LongFilter getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(LongFilter productTypeId) {
        this.productTypeId = productTypeId;
    }

    @Override
    public String toString() {
        return "ProductCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (price != null ? "price=" + price + ", " : "") +
                (remains != null ? "remains=" + remains + ", " : "") +
                (currencyTypeId != null ? "currencyTypeId=" + currencyTypeId + ", " : "") +
                (productStatusId != null ? "productStatusId=" + productStatusId + ", " : "") +
                (productTypeId != null ? "productTypeId=" + productTypeId + ", " : "") +
            "}";
    }

}
