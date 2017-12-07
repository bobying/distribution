package com.cloud.distribution.repository;

import com.cloud.distribution.domain.PayType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PayType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PayTypeRepository extends JpaRepository<PayType, Long>, JpaSpecificationExecutor<PayType> {

}
