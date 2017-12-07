package com.cloud.distribution.repository;

import com.cloud.distribution.domain.OperatorType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the OperatorType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OperatorTypeRepository extends JpaRepository<OperatorType, Long>, JpaSpecificationExecutor<OperatorType> {

}
