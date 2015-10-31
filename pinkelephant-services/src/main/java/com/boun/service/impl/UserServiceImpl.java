package com.boun.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boun.app.common.ErrorCode;
import com.boun.app.util.KeyUtils;
import com.boun.data.mongo.model.User;
import com.boun.data.mongo.repository.UserRepository;
import com.boun.data.session.PinkElephantSession;
import com.boun.http.request.AuthenticationRequest;
import com.boun.http.request.CreateUserRequest;
import com.boun.http.response.ActionResponse;
import com.boun.http.response.LoginResponse;
import com.boun.service.PinkElephantService;
import com.boun.service.UserService;

@Service
public class UserServiceImpl extends PinkElephantService implements UserService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepository userRepository;

	@Override
	public ActionResponse createUser(CreateUserRequest request) {

		ActionResponse response = new ActionResponse();

		if (!PinkElephantSession.getInstance().validateToken(request.getAuthToken())) {
			response.setAcknowledge(false);
			response.setMessage(ErrorCode.OPERATION_NOT_ALLOWED.getMessage());
			return response;
		}

		try {
			User user = userRepository.findByUsername(request.getUser().getUsername());
			if(user != null){
				response.setAcknowledge(false);
				response.setMessage(ErrorCode.DUPLICATE_USER.getMessage());
				return response;
			}

			userRepository.save(request.getUser());
			response.setAcknowledge(true);
		} catch (Throwable e) {
			response.setAcknowledge(false);
			response.setMessage(e.getMessage());

			logger.error("Error in createUser()", e);
		}

		return response;
	}

	@Override
	public LoginResponse authenticate(AuthenticationRequest request) {

		LoginResponse response = new LoginResponse();
		try {
			User user = userRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword());

			if (user == null) {
				response.setAcknowledge(false);
				response.setMessage(ErrorCode.USER_NOT_FOUND.getMessage());
				return response;
			}

			response.setAcknowledge(true);
			response.setToken(KeyUtils.currentTimeUUID().toString());

			PinkElephantSession.getInstance().addToken(response.getToken(), user);

		} catch (Throwable e) {
			response.setAcknowledge(false);
			response.setMessage(e.getMessage());

			logger.error("Error in authenticate()", e);
		}

		return response;
	}
}
