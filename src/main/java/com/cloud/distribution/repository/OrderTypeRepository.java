package com.cloud.distribution.repository;

import com.cloud.distribution.domain.OrderType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the OrderType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderTypeRepository extends JpaRepository<OrderType, Long>, JpaSpecificationExecutor<OrderType> {

}
