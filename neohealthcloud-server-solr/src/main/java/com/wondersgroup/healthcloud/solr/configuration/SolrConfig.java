package com.wondersgroup.healthcloud.solr.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

/**
 * Created by nick on 2017/5/9.
 */
@EnableAutoConfiguration
@EnableSolrRepositories(basePackages = {"com.wondersgroup.healthcloud.solr.repository"}, multicoreSupport = true,
        schemaCreationSupport = true)
public class SolrConfig {

}
