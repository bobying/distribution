package com.cloud.distribution.repository;

import com.cloud.distribution.domain.MerchantStatus;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the MerchantStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MerchantStatusRepository extends JpaRepository<MerchantStatus, Long>, JpaSpecificationExecutor<MerchantStatus> {

}
