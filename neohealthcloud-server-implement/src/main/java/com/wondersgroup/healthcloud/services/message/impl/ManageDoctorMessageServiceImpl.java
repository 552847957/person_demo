package com.wondersgroup.healthcloud.services.message.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorMessage;
import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorPushMessage;
import com.wondersgroup.healthcloud.jpa.repository.message.DoctorMessageRepository;
import com.wondersgroup.healthcloud.jpa.repository.message.DoctorPushMessageRepository;
import com.wondersgroup.healthcloud.services.doctormessage.ManageDoctorMessageService;

/**
 * Created by qiujun on 2015/9/10.
 */
@Service("manageDoctorMessageService")
public class ManageDoctorMessageServiceImpl implements ManageDoctorMessageService {

	@Autowired
	DoctorMessageRepository doctorMessageRepository;

	@Autowired
	DoctorPushMessageRepository doctorPushMessageRepository;

	@Override
	public boolean addDoctorMessage(DoctorMessage message) {
		doctorMessageRepository.save(message);
		return true;
	}

	@Override
	public boolean batchAddDoctorMessages(List<DoctorMessage> messages) {
		doctorMessageRepository.save(messages);
		return true;
	}

	@Override
	public List<DoctorMessage> queryDoctorMessageByTpye(String type, String receiveId) {
		Map<String, Object> parm = Maps.newHashMap();
		parm.put("msgType", type);
		parm.put("receiveId", receiveId);
		return doctorMessageRepository.findByMsgTypeAndReceiveId(type, receiveId,
				new Sort(Direction.DESC, "updateDate"));
	}

	@Override
	public boolean deleteDoctorPushMessage(List<String> pushMessages) {
		for (String id : pushMessages) {
			doctorPushMessageRepository.delete(id);
		}
		return true;
	}

	@Override
	public boolean addDoctorPushMessage(DoctorPushMessage pushMessage) {
		doctorPushMessageRepository.save(pushMessage);
		return true;
	}

	@Override
	public List<DoctorPushMessage> getAllPushMessages() {
		return doctorPushMessageRepository.findAll();
	}


	
}
