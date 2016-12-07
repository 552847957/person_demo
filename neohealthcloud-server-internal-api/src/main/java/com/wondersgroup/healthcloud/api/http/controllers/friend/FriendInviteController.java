package com.wondersgroup.healthcloud.api.http.controllers.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.services.friend.FriendInviteService;

@RestController
@RequestMapping("/api/friend")
public class FriendInviteController {

	@Autowired
	FriendInviteService friendInviteService;

	@RequestMapping(value = "/invite", method = RequestMethod.GET)
	public Object invite(String userId, String mobileNum) {
		return friendInviteService.invite(userId, mobileNum);
	}

}
