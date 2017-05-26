package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessmentRemind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2016/12/14.
 */
public interface DiabetesAssessmentRemindRepository extends JpaRepository<DiabetesAssessmentRemind, String>, JpaSpecificationExecutor<DiabetesAssessmentRemind> {
    @Query(nativeQuery = true,
            value = "select uid from (\n" +
                    "   select * from app_tb_patient_assessment where uid in ?1 and del_flag = '0' order by create_date desc)t1\n" +
                    "where not EXISTS(" +
                    "   select * from app_tb_diabetes_assessment_remind t2 where t2.registerid = t1.uid " +
                    "   and t2.type = ?2 and t2.create_date > t1.create_date and t2.del_flag = '0')\n" +
                    "GROUP BY uid")
    List<String> findScreeningByRegisterId(String[] registerIds,Integer type);

    @Query(nativeQuery = true,
            value=  "select registerid from app_tb_report_follow t1\n" +
                    "where t1.del_flag = '0' and t1.registerid in ?1\n" +
                    " and not EXISTS(" +
                    "   select * from app_tb_diabetes_assessment_remind t2 where t2.registerid = t1.registerid " +
                    "   and t2.type = ?2 and t2.create_date > t1.create_date and t2.del_flag = '0')")
    List<String> findFollowByRegisterId(String[] registerIds, Integer type);
}
