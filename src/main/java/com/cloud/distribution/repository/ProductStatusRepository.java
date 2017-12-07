package com.cloud.distribution.repository;

import com.cloud.distribution.domain.ProductStatus;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ProductStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductStatusRepository extends JpaRepository<ProductStatus, Long>, JpaSpecificationExecutor<ProductStatus> {

}
