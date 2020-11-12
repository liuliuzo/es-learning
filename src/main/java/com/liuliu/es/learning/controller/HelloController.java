package com.liuliu.es.learning.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author liuliu
 *
 */
@RestController
public class HelloController {

	@Autowired
	private CustomRepository customRepository;

	@Autowired
	private ElasticsearchRestTemplate elasticsearchRestTemplate;
	
	@RequestMapping(value = "/esTest")
	public String esTest() throws JsonProcessingException {		
		EsDemo esDemo = new EsDemo();
		esDemo.setId(UUID.randomUUID().toString());
		esDemo.setName("tz");
		esDemo.setUsername("admin");
		Instant start = Instant.now();
		EsDemo save = customRepository.save(esDemo);
		Instant end = Instant.now();
		System.out.println(save.toString() + "cost:" + Duration.between(start, end).toMillis());
		ObjectMapper objectMapper = new ObjectMapper();
		String s = objectMapper.writeValueAsString(save);
		return s;
	}
	
	@RequestMapping(value = "/esTest02")
	public String esTest02(){
	    elasticsearchRestTemplate.createIndex(EsDemo.class);
		EsDemo esDemo = new EsDemo();
		esDemo.setId(UUID.randomUUID().toString());
		esDemo.setName("tz");
		esDemo.setUsername("admin");
		IndexQuery indexQuery = new IndexQueryBuilder()
				.withId(esDemo.getId())
				.withObject(esDemo)
				.build();
		Instant start = Instant.now();
		String result= elasticsearchRestTemplate.index(indexQuery);
		Instant end = Instant.now();
		System.out.println(result + " cost:" + Duration.between(start, end).toMillis());
		return result;
	}

    @RequestMapping(value = "/perftest")
    public void perftest(){
        elasticsearchRestTemplate.createIndex(EsDemo.class);
        EsDemo esDemo = new EsDemo();
        esDemo.setId(UUID.randomUUID().toString());
        esDemo.setName("tz");
        esDemo.setUsername("admin");
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(esDemo.getId())
                .withObject(esDemo)
                .build();
        while (true) {
            Instant start = Instant.now();
            String result1 = elasticsearchRestTemplate.index(indexQuery);
            Instant end = Instant.now();
            System.out.println(result1 + " cost:" + Duration.between(start, end).toMillis());
        }
    }
    
    @RequestMapping(value = "/perftest02")
    public void perftest02(){
        while (true) {
            EsDemo esDemo = new EsDemo();
            esDemo.setId(UUID.randomUUID().toString());
            esDemo.setName("tz");
            esDemo.setUsername("admin");
            Instant start = Instant.now();
            EsDemo save = customRepository.save(esDemo);
            Instant end = Instant.now();
            System.out.println(save.toString() + "cost:" + Duration.between(start, end).toMillis());
        }
    }
}
