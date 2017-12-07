package com.cloud.distribution.service.dto;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the Merchant entity.
 */
public class MerchantDTO implements Serializable {

    private Long id;

    private String name;

    @Lob
    private String desc;

    private Long level;

    private Long userId;

    private String address;

    private String mobile;

    private Long merchantTypeId;

    private String merchantTypeName;

    private Long merchantAuditStatusId;

    private String merchantAuditStatusName;

    private Long merchantStatusId;

    private String merchantStatusName;

    private Long parentId;

    private String parentName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getMerchantTypeId() {
        return merchantTypeId;
    }

    public void setMerchantTypeId(Long merchantTypeId) {
        this.merchantTypeId = merchantTypeId;
    }

    public String getMerchantTypeName() {
        return merchantTypeName;
    }

    public void setMerchantTypeName(String merchantTypeName) {
        this.merchantTypeName = merchantTypeName;
    }

    public Long getMerchantAuditStatusId() {
        return merchantAuditStatusId;
    }

    public void setMerchantAuditStatusId(Long merchantAuditStatusId) {
        this.merchantAuditStatusId = merchantAuditStatusId;
    }

    public String getMerchantAuditStatusName() {
        return merchantAuditStatusName;
    }

    public void setMerchantAuditStatusName(String merchantAuditStatusName) {
        this.merchantAuditStatusName = merchantAuditStatusName;
    }

    public Long getMerchantStatusId() {
        return merchantStatusId;
    }

    public void setMerchantStatusId(Long merchantStatusId) {
        this.merchantStatusId = merchantStatusId;
    }

    public String getMerchantStatusName() {
        return merchantStatusName;
    }

    public void setMerchantStatusName(String merchantStatusName) {
        this.merchantStatusName = merchantStatusName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long merchantId) {
        this.parentId = merchantId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String merchantName) {
        this.parentName = merchantName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MerchantDTO merchantDTO = (MerchantDTO) o;
        if(merchantDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), merchantDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MerchantDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", desc='" + getDesc() + "'" +
            ", level=" + getLevel() +
            ", userId=" + getUserId() +
            ", address='" + getAddress() + "'" +
            ", mobile='" + getMobile() + "'" +
            "}";
    }
}
