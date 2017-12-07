package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.MerchantType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the MerchantType entity.
 */
public interface MerchantTypeSearchRepository extends ElasticsearchRepository<MerchantType, Long> {
}
