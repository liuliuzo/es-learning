package com.liuliu.es.learning.controller;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomRepository extends ElasticsearchRepository<EsDemo,String>{
}
