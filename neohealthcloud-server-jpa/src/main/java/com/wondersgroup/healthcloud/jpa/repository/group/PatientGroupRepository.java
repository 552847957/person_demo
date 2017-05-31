package com.wondersgroup.healthcloud.jpa.repository.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.group.PatientGroup;

public interface PatientGroupRepository extends JpaRepository<PatientGroup, Integer> {
    
    @Query(nativeQuery=true,value="SELECT * FROM app_tb_patient_group p WHERE p.doctor_id=?1 AND p.del_flag='0' ORDER BY p.rank ASC")
    List<PatientGroup> getPatientGroupByDoctorId(String doctorId);
    
    @Query(nativeQuery=true,value="SELECT name FROM app_tb_patient_group p WHERE p.doctor_id=?1 AND p.name=?2")
    String findIsNameRepeated(String doctorId,String name);
    
    @Query(nativeQuery=true,value="SELECT t1.* FROM app_tb_patient_group t1 INNER JOIN app_tb_sign_user_doctor_group t2 ON t1.id=t2.group_id WHERE t1.doctor_id=?1 AND t1.id=?2 AND t2.del_flag='0' AND t1.del_flag='0' ")
    List<PatientGroup> getIsPatientGroupByDoctorIdAndId(String doctorId,Integer id);
    
    @Query(nativeQuery=true,value="SELECT MAX(rank) FROM app_tb_patient_group p WHERE p.doctor_id=?1")
    int getMaxSortByDoctorId(String doctorId);
    
    @Query(nativeQuery=true,value="SELECT COUNT(1) FROM app_tb_patient_group p WHERE p.doctor_id=?1 AND p.del_flag='0' ")
    int getGroupNumByDoctorId(String doctorId);
}
