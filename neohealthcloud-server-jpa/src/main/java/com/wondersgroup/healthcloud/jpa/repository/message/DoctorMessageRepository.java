package com.wondersgroup.healthcloud.jpa.repository.message;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorMessage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DoctorMessageRepository extends JpaRepository<DoctorMessage, String>{
	
	List<DoctorMessage> findByMsgTypeAndReceiveId(String msgType, String receiveId, Sort sort);


	@Query("select count(m) from DoctorMessage m where m.delFlag = '0' and m.isRead = 0 and m.receiveId = ?1")
	int countUnreadMsgByUid(String uid);

	@Query("select count(m) from DoctorMessage m where m.delFlag = '0'" +
			" and m.isRead = 0 and m.receiveId = ?1 and m.msgType = ?2")
	int countUnreadMsgByUidAndType(String uid, String msgType);

	@Transactional
	@Modifying
	@Query(" update DoctorMessage set delFlag ='1' where id = ?1 ")
	void deleteDoctorMsgById(String id);

	@Transactional
	@Modifying
	@Query(" update DoctorMessage set delFlag ='1' where receiveId = ?1 and msgType = ?2 ")
	void deleteDoctorMsgByMsgType(String uid, String msgType);

	@Transactional
	@Modifying
	@Query(" update DoctorMessage set isRead ='1' where receiveId = ?1 and msgType = ?2  and isRead = '0' ")
	void setMsgIsReadByMsgType(String uid, String msgType);
}
