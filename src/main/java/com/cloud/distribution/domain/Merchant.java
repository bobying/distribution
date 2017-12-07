package com.cloud.distribution.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


/**
 * A Merchant.
 */
@Entity
@Table(name = "merchant")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "merchant")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "jhi_desc")
    private String desc;

    @Column(name = "jhi_level")
    private Long level;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "address")
    private String address;

    @Column(name = "mobile")
    private String mobile;

    @ManyToOne
    private MerchantType merchantType;

    @ManyToOne
    private MerchantAuditStatus merchantAuditStatus;

    @ManyToOne
    private MerchantStatus merchantStatus;

    @ManyToOne
    private Merchant parent;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Merchant name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public Merchant desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getLevel() {
        return level;
    }

    public Merchant level(Long level) {
        this.level = level;
        return this;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getUserId() {
        return userId;
    }

    public Merchant userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public Merchant address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public Merchant mobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public MerchantType getMerchantType() {
        return merchantType;
    }

    public Merchant merchantType(MerchantType merchantType) {
        this.merchantType = merchantType;
        return this;
    }

    public void setMerchantType(MerchantType merchantType) {
        this.merchantType = merchantType;
    }

    public MerchantAuditStatus getMerchantAuditStatus() {
        return merchantAuditStatus;
    }

    public Merchant merchantAuditStatus(MerchantAuditStatus merchantAuditStatus) {
        this.merchantAuditStatus = merchantAuditStatus;
        return this;
    }

    public void setMerchantAuditStatus(MerchantAuditStatus merchantAuditStatus) {
        this.merchantAuditStatus = merchantAuditStatus;
    }

    public MerchantStatus getMerchantStatus() {
        return merchantStatus;
    }

    public Merchant merchantStatus(MerchantStatus merchantStatus) {
        this.merchantStatus = merchantStatus;
        return this;
    }

    public void setMerchantStatus(MerchantStatus merchantStatus) {
        this.merchantStatus = merchantStatus;
    }

    public Merchant getParent() {
        return parent;
    }

    public Merchant parent(Merchant merchant) {
        this.parent = merchant;
        return this;
    }

    public void setParent(Merchant merchant) {
        this.parent = merchant;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Merchant merchant = (Merchant) o;
        if (merchant.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), merchant.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Merchant{" +
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
