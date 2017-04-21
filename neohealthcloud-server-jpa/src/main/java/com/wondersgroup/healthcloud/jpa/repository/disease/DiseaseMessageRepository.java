package com.wondersgroup.healthcloud.jpa.repository.disease;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.jpa.entity.disease.DiseaseMessage;


public interface DiseaseMessageRepository extends JpaRepository<DiseaseMessage, String> {

    @Transactional(readOnly = true)
    @Query(nativeQuery = true, value = "select * from app_tb_disease_message  where receiver_uid = ?1 and msg_type = '3' and DATE_FORMAT(create_time,'%Y-%m-%d')")
    DiseaseMessage getDiseaseMessageByToday(String receiverUid);



}