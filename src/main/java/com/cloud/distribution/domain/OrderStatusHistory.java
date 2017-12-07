package com.cloud.distribution.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;


/**
 * A OrderStatusHistory.
 */
@Entity
@Table(name = "order_status_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "orderstatushistory")
public class OrderStatusHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modified_time")
    private ZonedDateTime modifiedTime;

    @Lob
    @Column(name = "jhi_desc")
    private String desc;

    @Column(name = "operator_code")
    private String operatorCode;

    @ManyToOne
    private OperatorType operatorType;

    @ManyToOne
    private Order order;

    @ManyToOne
    private OrderStatus oldStatus;

    @ManyToOne
    private OrderStatus newStatus;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getModifiedTime() {
        return modifiedTime;
    }

    public OrderStatusHistory modifiedTime(ZonedDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
        return this;
    }

    public void setModifiedTime(ZonedDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getDesc() {
        return desc;
    }

    public OrderStatusHistory desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public OrderStatusHistory operatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
        return this;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public OrderStatusHistory operatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
        return this;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public Order getOrder() {
        return order;
    }

    public OrderStatusHistory order(Order order) {
        this.order = order;
        return this;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public OrderStatusHistory oldStatus(OrderStatus orderStatus) {
        this.oldStatus = orderStatus;
        return this;
    }

    public void setOldStatus(OrderStatus orderStatus) {
        this.oldStatus = orderStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public OrderStatusHistory newStatus(OrderStatus orderStatus) {
        this.newStatus = orderStatus;
        return this;
    }

    public void setNewStatus(OrderStatus orderStatus) {
        this.newStatus = orderStatus;
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
        OrderStatusHistory orderStatusHistory = (OrderStatusHistory) o;
        if (orderStatusHistory.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), orderStatusHistory.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OrderStatusHistory{" +
            "id=" + getId() +
            ", modifiedTime='" + getModifiedTime() + "'" +
            ", desc='" + getDesc() + "'" +
            ", operatorCode='" + getOperatorCode() + "'" +
            "}";
    }
}
