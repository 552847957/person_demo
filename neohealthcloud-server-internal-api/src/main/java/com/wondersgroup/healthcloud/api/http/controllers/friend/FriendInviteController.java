package com.wondersgroup.healthcloud.api.http.controllers.friend;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.services.friend.FriendInviteService;

@RestController
@RequestMapping("/api/friend")
public class FriendInviteController {

	@Autowired
	FriendInviteService friendInviteService;

	@RequestMapping(value = "/invite", method = RequestMethod.GET)
	public Object invite(String userId, String mobileNum, String code) {
		JsonResponseEntity<String> responseEntity = new JsonResponseEntity<>();
		boolean varify = code == null || friendInviteService.verifySmsCode(mobileNum, code);
		if (varify) {
//			Map<String, Object> map = friendInviteService.invite(userId, mobileNum);
			Map<String, Object> map = friendInviteService.inviteNew(userId, mobileNum);
			return map;
		} else {
			responseEntity.setCode(1003);
			responseEntity.setMsg("验证码错误");
			return responseEntity;
		}
	}
	

	@RequestMapping(value = "/sms", method = RequestMethod.GET)
	public Object sms(String mobileNum) {
		JsonResponseEntity<String> responseEntity = new JsonResponseEntity<>();
		friendInviteService.sendCode(mobileNum);
		return responseEntity;
	}

}
