package com.wondersgroup.healthcloud.services.doctormessage.impl;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorMessage;
import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorPushMessage;
import com.wondersgroup.healthcloud.jpa.repository.message.DoctorMessageRepository;
import com.wondersgroup.healthcloud.jpa.repository.message.DoctorPushMessageRepository;
import com.wondersgroup.healthcloud.services.doctormessage.ManageDoctorMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by qiujun on 2015/9/10.
 */
@Service("manageDoctorMessageService")
public class ManageDoctorMessageServiceImpl implements ManageDoctorMessageService {

	@Autowired
	DoctorMessageRepository doctorMessageRepository;

	@Autowired
	DoctorPushMessageRepository doctorPushMessageRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

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

	/**
	 * 查询医生是否含有未读信息
	 * @param uid
	 * @return
	 */
	@Override
	public int countUnreadMsgByUid(String uid) {
		return doctorMessageRepository.countUnreadMsgByUid(uid);
	}

	/**
	 * 查询医生 所有类型信息的最新一条
	 * @param uid
	 * @return
	 */
	@Override
	public List<DoctorMessage> findTypeMsgListByUid(String uid) {
		String sql = "select * from ( select * from app_tb_doctor_message" +
				" where receiveId = '%s' and del_flag = '0'" +
				" order by updateDate desc)" +
				" a group by msgType order by msgType";
		sql = String.format(sql,uid);
		return jdbcTemplate.queryForList(sql, DoctorMessage.class);
	}

	/**
	 * 查询医生某个信息类型下面未读信息数量
	 * @param uid
	 * @param msgType
	 * @return
	 */
	@Override
	public int countUnreadMsgByUidAndType(String uid, String msgType) {
		return doctorMessageRepository.countUnreadMsgByUidAndType(uid,msgType);
	}

	/**
	 * 根据消息类型医生Id 分页查询消息列表
	 * @param uid
	 * @param msgType
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Override
	public List<DoctorMessage> findMsgListByUidAndType(String uid, String msgType, Integer pageNo, int pageSize) {
		String sql = "select * from app_tb_doctor_message a where" +
				" a.receiveId = '%s'" +
				" and a.msgType = '%s'" +
				" order by a.updateDate desc " +
				" limit %d,%d";
		sql = String.format(sql,uid,msgType,pageNo*pageSize,pageSize);
		return jdbcTemplate.queryForList(sql, DoctorMessage.class);
	}

	/**
	 * 删除消息
	 * @param id
	 */
	@Override
	public void deleteDoctorMsgById(String id) {
		doctorMessageRepository.deleteDoctorMsgById(id);
	}

	/**
	 * 根据类型删除消息
	 * @param uid
	 * @param msgType
	 */
	@Override
	public void deleteDoctorMsgByMsgType(String uid, String msgType) {
		doctorMessageRepository.deleteDoctorMsgByMsgType(uid,msgType);
	}

	/**
	 * 根据医生Id和信息类型 设置所有未读信息为已读
	 * @param uid
	 * @param msgType
	 */
	@Override
	public void setMsgIsReadByMsgType(String uid, String msgType) {
		doctorMessageRepository.setMsgIsReadByMsgType(uid,msgType);
	}


}
