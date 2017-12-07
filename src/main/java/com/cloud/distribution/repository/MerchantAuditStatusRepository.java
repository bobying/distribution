package com.cloud.distribution.repository;

import com.cloud.distribution.domain.MerchantAuditStatus;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the MerchantAuditStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MerchantAuditStatusRepository extends JpaRepository<MerchantAuditStatus, Long>, JpaSpecificationExecutor<MerchantAuditStatus> {

}
