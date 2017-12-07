package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.PayType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the PayType entity.
 */
public interface PayTypeSearchRepository extends ElasticsearchRepository<PayType, Long> {
}
