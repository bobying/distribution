package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.MerchantAuditStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the MerchantAuditStatus entity.
 */
public interface MerchantAuditStatusSearchRepository extends ElasticsearchRepository<MerchantAuditStatus, Long> {
}
