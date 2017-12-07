package com.cloud.distribution.repository;

import com.cloud.distribution.domain.MerchantType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the MerchantType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MerchantTypeRepository extends JpaRepository<MerchantType, Long>, JpaSpecificationExecutor<MerchantType> {

}
