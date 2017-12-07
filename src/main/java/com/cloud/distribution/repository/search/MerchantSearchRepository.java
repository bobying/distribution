package com.cloud.distribution.repository.search;

import com.cloud.distribution.domain.Merchant;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Merchant entity.
 */
public interface MerchantSearchRepository extends ElasticsearchRepository<Merchant, Long> {
}
