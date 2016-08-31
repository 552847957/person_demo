package com.wondersgroup.healthcloud.jpa.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorPushMessage;

public interface DoctorPushMessageRepository extends JpaRepository<DoctorPushMessage, String>{

}
