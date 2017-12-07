package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.MerchantStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the MerchantStatus entity.
 */
public interface MerchantStatusSearchRepository extends ElasticsearchRepository<MerchantStatus, Long> {
}
