package com.boun.service.impl;

import java.util.Date;
import java.util.List;

import com.boun.http.response.ListNoteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boun.app.common.ErrorCode;
import com.boun.app.exception.PinkElephantRuntimeException;
import com.boun.data.mongo.model.Group;
import com.boun.data.mongo.model.Meeting;
import com.boun.data.mongo.model.Note;
import com.boun.data.mongo.model.Resource;
import com.boun.data.mongo.model.TaggedEntity;
import com.boun.data.mongo.repository.NoteRepository;
import com.boun.data.session.PinkElephantSession;
import com.boun.http.request.BasicQueryRequest;
import com.boun.http.request.CreateNoteRequest;
import com.boun.http.request.UpdateNoteRequest;
import com.boun.http.response.NoteResponse;
import com.boun.service.GroupService;
import com.boun.service.MeetingService;
import com.boun.service.NoteService;
import com.boun.service.PinkElephantTaggedService;
import com.boun.service.ResourceService;
import com.boun.service.TagService;

@Service
public class NoteServiceImpl extends PinkElephantTaggedService implements NoteService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private GroupService groupService;

	@Autowired
	private MeetingService meetingService;

	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private TagService tagService;

	@Override
	public void save(TaggedEntity entity) {
		noteRepository.save((Note)entity);
	}
	
	@Override
	public Note findById(String id) {
		Note note = noteRepository.findOne(id);

		if(note == null) {
			throw new PinkElephantRuntimeException(400, ErrorCode.NOTE_NOT_FOUND, "todo dev message");
		}

		return note;
	}

	@Override
	public Note createNote(CreateNoteRequest request) {

		validate(request);

		//TODO find group method will throw an exception if entity not found
		Group group = groupService.findById(request.getGroupId());
		//TODO check if user is in this group

		//TODO find meeting method will throw an exception if entity not found
		Meeting meeting = meetingService.findById(request.getMeetingId());


		List<Resource> resources = null;
		if(request.getResourceIds() != null)
			resources = resourceService.findByIds(request.getResourceIds());

		Note note = new Note();

		note.setTitle(request.getTitle());
		note.setDescription(request.getText());
		note.setGroup(group);
		note.setCreator(PinkElephantSession.getInstance().getUser(request.getAuthToken()));
		note.setCreatedAt(new Date());
		note.setMeeting(meeting);
		note.setResources(resources);
		note.setTagList(request.getTagList());
		note.setIsPinned(request.getIsPinned());
		
		note = noteRepository.save(note);
		tagService.tag(request.getTagList(), note, true);

		return note;

	}

	@Override
	public Note updateNote(UpdateNoteRequest request) {

		validate(request);

		Note note = findById(request.getId());

		//TODO find group method will throw an exception if entity not found
		Group group = groupService.findById(request.getGroupId());
		//TODO check if user is in this group

		//TODO find meeting method will throw an exception if entity not found
		Meeting meeting = meetingService.findById(request.getMeetingId());
		List<Resource> resources = resourceService.findByIds(request.getResourceIds());

		note.setTitle(request.getTitle());
		note.setDescription(request.getText());
		note.setGroup(group);
		updateTag(note, request.getTagList());
		
		//note.setCreator(PinkElephantSession.getInstance().getUser(request.getAuthToken()));
		//note.setCreatedAt(new Date());
		//TODO modifiedDate, modifiedBy

		note.setMeeting(meeting);
		note.setResources(resources);
		note.setIsPinned(request.getIsPinned());

		note = noteRepository.save(note);

		return note;
	}
	
	@Override
	protected TagService getTagService() {
		return tagService;
	}

	@Override
	public NoteResponse queryNote(BasicQueryRequest request) {

		validate(request);

		Note note = findById(request.getId());

		NoteResponse noteResponse = new NoteResponse();
		noteResponse.setId(note.getId());
		noteResponse.setCreatedAt(note.getCreatedAt());
		noteResponse.setTagList(note.getTagList());
		noteResponse.setText(note.getDescription());
		noteResponse.setTitle(note.getTitle());

		return noteResponse;
	}

	@Override
	public ListNoteResponse queryNotesOfGroup(BasicQueryRequest request) {

		validate(request);

		Group group = groupService.findById(request.getId());

		ListNoteResponse response = new ListNoteResponse();

		List<Note> noteList = noteRepository.findNotes(group.getId());

		for (Note note : noteList) {
			response.addNote(note);
		}

		return response;
	}
}
