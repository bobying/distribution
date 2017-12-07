package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.OrderStatusHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the OrderStatusHistory entity.
 */
public interface OrderStatusHistorySearchRepository extends ElasticsearchRepository<OrderStatusHistory, Long> {
}
