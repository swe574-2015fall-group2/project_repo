package com.boun.data.mongo.model;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Document(collection = "discussion")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Discussion extends TaggedEntity{

	public Discussion(){
		super(EntityType.DISCUSSION);
	}
	
	@DBRef
	private Group group;
	
	@DBRef
	private User creator;
	
	private String name;
	private String description;
	private Date creationTime;
	private Date updateTime;
}
