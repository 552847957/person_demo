package com.wondersgroup.healthcloud.services.doctormessage.impl;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.enums.DoctorMsgTypeEnum;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorMessage;
import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorPushMessage;
import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import com.wondersgroup.healthcloud.jpa.repository.appointment.DoctorRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.message.DoctorMessageRepository;
import com.wondersgroup.healthcloud.jpa.repository.message.DoctorPushMessageRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctormessage.ManageDoctorMessageService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
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
	private DoctorAccountRepository doctorAccountRepository;

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
				" where receiveId = '%s' and del_flag = '0' and msgType !='2' " +
				" order by updateDate desc)" +
				" a group by msgType order by msgType";
		sql = String.format(sql,uid);
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(DoctorMessage.class));
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
				" and a.msgType = '%s' and a.del_flag = '0' " +
				" order by a.updateDate desc " +
				" limit %d,%d";
		sql = String.format(sql,uid,msgType,pageNo*pageSize,pageSize+1);
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(DoctorMessage.class));
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

	/**
	 * 保存
	 * @param
	 * @param type 1-提问 2-追问
	 */
	@Override
	public void saveDoctorQuestionMessage(String doctorId,String questionId, int type) {
		DoctorAccount doctorAccount = doctorAccountRepository.findOne(doctorId);
		DoctorMessage doctorMessage = new DoctorMessage();
		doctorMessage.setId(IdGen.uuid());
		doctorMessage.setDelFlag("0");
		doctorMessage.setIsRead(0);
		doctorMessage.setMsgType(DoctorMsgTypeEnum.msgTypeQuestion.getTypeCode());//问诊提醒
		if(type==1){
			doctorMessage.setTitle("问诊提醒");
			doctorMessage.setContent("有患者向您进行了咨询,请查看。");
		}else{
			doctorMessage.setTitle("追问提醒");
			doctorMessage.setContent("您回复的患者有了新的追问,请查看。");
		}
		doctorMessage.setUpdateDate(new DateTime(new Date()).toString("yyyy-MM-dd HH:mm:ss"));

		doctorMessage.setUrlFragment(String.format(DoctorMsgTypeEnum.msgTypeQuestion.getUrlFragment(),questionId));
		doctorMessage.setReceive(doctorAccount.getName());
		doctorMessage.setReceiveId(doctorId);
		doctorMessageRepository.save(doctorMessage);
	}


}
