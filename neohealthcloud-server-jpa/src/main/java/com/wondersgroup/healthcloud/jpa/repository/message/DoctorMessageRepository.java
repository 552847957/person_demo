package com.wondersgroup.healthcloud.jpa.repository.message;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorMessage;

public interface DoctorMessageRepository extends JpaRepository<DoctorMessage, String>{
	
	List<DoctorMessage> findByMsgTypeAndReceiveId(String msgType, String receiveId, Sort sort);
			
			
			

}
