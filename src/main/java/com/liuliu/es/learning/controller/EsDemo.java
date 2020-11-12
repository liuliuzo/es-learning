package com.liuliu.es.learning.controller;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 
 * @author liuliu
 *
 */
@Data
@Document(indexName = "test", type = "test", createIndex = false)
public class EsDemo {
	
	@Id
	private String id;
	
	@Field(type = FieldType.Keyword)
	private String username;
	
	@Field(type = FieldType.Keyword)
	private String name;

    @Override
    public String toString() {
        return "EsDemo [id=" + id + ", username=" + username + ", name=" + name + "]";
    }
}
