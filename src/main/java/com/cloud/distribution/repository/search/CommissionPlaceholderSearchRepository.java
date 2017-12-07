package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.CommissionPlaceholder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the CommissionPlaceholder entity.
 */
public interface CommissionPlaceholderSearchRepository extends ElasticsearchRepository<CommissionPlaceholder, Long> {
}
