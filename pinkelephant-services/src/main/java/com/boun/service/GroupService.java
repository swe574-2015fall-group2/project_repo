package com.boun.service;

import com.boun.data.mongo.model.Group;
import com.boun.http.request.BaseRequest;
import com.boun.http.request.BasicQueryRequest;
import com.boun.http.request.CreateUpdateGroupRequest;
import com.boun.http.request.JoinLeaveGroupRequest;
import com.boun.http.request.TagRequest;
import com.boun.http.request.UploadImageRequest;
import com.boun.http.response.ActionResponse;
import com.boun.http.response.CreateResponse;
import com.boun.http.response.GetGroupResponse;
import com.boun.http.response.ListGroupResponse;

import java.util.List;

public interface GroupService {

	Group findById(String groupId);

	Group findByName(String groupName);

	CreateResponse createGroup(CreateUpdateGroupRequest request);
	
	ActionResponse updateGroup(CreateUpdateGroupRequest request);
	
	ActionResponse joinGroup(JoinLeaveGroupRequest request);
	
	ActionResponse leaveGroup(JoinLeaveGroupRequest request);
	
	ListGroupResponse getMyGroups(BaseRequest request);
	
	ListGroupResponse getAllGroups(BaseRequest request);

	ListGroupResponse getPopularGroups(BaseRequest request);

	ListGroupResponse getLatestGroups(BaseRequest request);
	
	ActionResponse uploadImage(UploadImageRequest request);
	
	GetGroupResponse queryGroup(BasicQueryRequest request);
	
	ActionResponse tag(TagRequest request);
	
	ListGroupResponse findRecommendedGroups(BaseRequest request);

	List<Group> findGroupsOfUser(String userId);
}
