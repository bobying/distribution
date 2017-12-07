package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.ProductStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ProductStatus entity.
 */
public interface ProductStatusSearchRepository extends ElasticsearchRepository<ProductStatus, Long> {
}
