package com.wondersgroup.healthcloud.jpa.repository.disease;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.jpa.entity.disease.DiseaseMessage;


public interface DiseaseMessageRepository extends JpaRepository<DiseaseMessage, String> {

    @Transactional(readOnly = true)
    @Query(nativeQuery = true, value = "select * from app_tb_disease_message  where receiver_uid = ?1 and msg_type = '3' and DATE_FORMAT(create_time,'%Y-%m-%d') = DATE_FORMAT(now(),'%Y-%m-%d') limit 1")
    DiseaseMessage getDiseaseMessageByToday(String receiverUid);


    @Transactional
    @Modifying
    @Query(" update DiseaseMessage set delFlag ='1',isRead='1' where id = ?2 and msgType=?1 ")
    void deleteMsg(String typeCode, Integer msgID);

    @Transactional
    @Modifying
    @Query(" update DiseaseMessage set delFlag ='1',isRead='1' where receiverUid = ?1 and msgType=?2  ")
    void deleteAllMsg(String uid, String typeCode);
}
