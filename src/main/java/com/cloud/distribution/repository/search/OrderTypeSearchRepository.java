package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.OrderType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the OrderType entity.
 */
public interface OrderTypeSearchRepository extends ElasticsearchRepository<OrderType, Long> {
}
