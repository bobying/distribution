package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.OrderStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the OrderStatus entity.
 */
public interface OrderStatusSearchRepository extends ElasticsearchRepository<OrderStatus, Long> {
}
