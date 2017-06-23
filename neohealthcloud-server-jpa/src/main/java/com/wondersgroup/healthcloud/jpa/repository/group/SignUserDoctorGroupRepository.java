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
    @Query(nativeQuery = true, value = "update app_tb_sign_user_doctor_group t set t.del_flag=?1,t.update_time=NOW() where t.group_id=?2 AND t.user_id=?3")
    void updateDoctorGroup(String delFlag, Integer groupId,String userId);
    
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update app_tb_sign_user_doctor_group t set t.del_flag=?1,t.update_time=NOW() where t.group_id=?2")
    void updateDoctorGroup(String delFlag, Integer groupId);
    
    @Query(nativeQuery = true, value = "SELECT t.group_id FROM app_tb_sign_user_doctor_group t INNER JOIN app_tb_patient_group p ON p.id=t.group_id WHERE t.user_id=?1 AND p.doctor_id=?2 AND t.del_flag='0' AND p.del_flag='0'")
    List<Integer> getGroupIdsByUserId(String userId, String doctorId);

    @Query(nativeQuery = true, value = "SELECT count(1) FROM app_tb_sign_user_doctor_group t WHERE t.group_id=?1 AND t.del_flag='0'")
    int getNumByGroupId(Integer groupId);

    @Query(nativeQuery = true, value = "SELECT * FROM app_tb_sign_user_doctor_group t WHERE t.user_id=?1 AND t.group_id=?2 AND t.del_flag=?3")
    SignUserDoctorGroup getIsSelectedByGroupIdAndUserId(String userId, Integer groupId, String delFlag);

    List<SignUserDoctorGroup> queryByDelFlagAndGroupIdOrderByUpdateTimeAsc(String delFlag, Integer groupId);

    SignUserDoctorGroup queryFirst1ByDelFlagAndUid(String delFlag, String uid);

    @Query(nativeQuery = true,value = "select u.* from app_tb_patient_group g inner join app_tb_sign_user_doctor_group u \n" +
            " on g.id = u.group_id " +
            " where u.del_flag = 0 and g.del_flag = 0 and " +
            " g.doctor_id = ?1 and u.user_id = ?2 limit 1")
    SignUserDoctorGroup queryByDoctorIdUid(String doctorId,String uId);
}
