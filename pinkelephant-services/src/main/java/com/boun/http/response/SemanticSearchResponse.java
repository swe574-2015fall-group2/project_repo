package com.boun.http.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.boun.data.mongo.model.TaggedEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SemanticSearchResponse {
	
	private List<SearchDetail> resultList;
	
	public void addDetail(TaggedEntity.EntityType type, String id, String description, String tag, float priority){
		if(resultList == null){
			resultList = new ArrayList<SearchDetail>();
		}
		SearchDetail detail = new SearchDetail(type, id, description, tag, priority);
		if(!resultList.contains(detail)){
			resultList.add(detail);	
		}
	}
	
	public List<SearchDetail> getResultList(){
		Collections.sort(resultList, new SearchDetailSort());
		return resultList;
	}
	
	@Data
	private static class SearchDetail{
		private TaggedEntity.EntityType type;
		
		private String id;
		private String description;
		private String tag;
		
		private float priority;
		
		public SearchDetail(TaggedEntity.EntityType type, String id, String description, String tag, float priority){
			this.type = type;
			this.id = id;
			this.description = description;
			this.priority = priority;
			this.tag = tag;
		}
		
		public boolean equals(Object o){
			if(o == null){
				return false;
			}
			SearchDetail idx = (SearchDetail)o;
			
			return (idx.getId().equalsIgnoreCase(this.getId())) && (idx.getType() == this.getType());
		}
		
		@Override
		public int hashCode() {
			int code = 7;
			code = 89 * code * this.getTag().hashCode();
			code = code * this.getType().hashCode();
			return code;
		}
	}
	
	private static class SearchDetailSort implements Comparator<SearchDetail> {

	    @Override
	    public int compare(SearchDetail o1, SearchDetail o2) {
	    	
	    	return (o1.getPriority() >= o2.getPriority()) ? -1 : 1;
	    }
	}
}
