package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.OperatorType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the OperatorType entity.
 */
public interface OperatorTypeSearchRepository extends ElasticsearchRepository<OperatorType, Long> {
}
