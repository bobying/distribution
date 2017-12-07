package com.cloud.distribution.repository;

import com.cloud.distribution.domain.CommissionPlaceholder;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the CommissionPlaceholder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommissionPlaceholderRepository extends JpaRepository<CommissionPlaceholder, Long>, JpaSpecificationExecutor<CommissionPlaceholder> {

}
