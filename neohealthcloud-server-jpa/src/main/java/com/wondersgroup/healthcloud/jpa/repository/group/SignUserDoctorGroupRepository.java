package com.wondersgroup.healthcloud.jpa.repository.group;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.group.SignUserDoctorGroup;

public interface SignUserDoctorGroupRepository extends JpaRepository<SignUserDoctorGroup, Integer> {
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update app_tb_sign_user_doctor_group t set t.del_flag='1' where t.group_id=?1 ")
    void updateDoctorGroup(Integer groupId);
    
    @Query(nativeQuery = true,value="SELECT t.group_id FROM app_tb_sign_user_doctor_group t WHERE t.user_id=?1 AND t.del_flag='0'")
    List<Integer>  getGroupIdsByUserId(String userId);
    
    @Query(nativeQuery = true,value="SELECT count(1) FROM app_tb_sign_user_doctor_group t WHERE t.group_id=?1 AND t.del_flag='0'")
    int getNumByGroupId(Integer groupId);
}
