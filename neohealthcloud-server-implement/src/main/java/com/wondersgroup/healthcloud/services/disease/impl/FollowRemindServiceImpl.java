package com.wondersgroup.healthcloud.services.disease.impl;

import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentHospital;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.appointment.HospitalRepository;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRemindRepository;
import com.wondersgroup.healthcloud.services.disease.FollowRemindService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2017/5/24.
 */
@Service("followRemindService")
public class FollowRemindServiceImpl implements FollowRemindService{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DiabetesAssessmentRemindRepository remindRepo;


    /**
     * 获取随访列表数据
     * @param pageNo
     * @param pageSize
     * @param signStatus 签约状态 1：已经签约居民，0：未签约居民，null：所有类型的居民
     * @param diseaseType 慢病类型 1：糖尿病，2：高血压，3：脑卒中，null：所有类型的居民
     * @return
     */
    @Override
    public List<Map<String, Object>> findFollow(Integer pageNo, int pageSize, Integer signStatus, String diseaseType, DoctorInfo doctorInfo,DoctorAccount doctorAccount) {
        String sql = "select t1.follow_date,t1.remind_end_date,t2.registerid," +
                " t3.name,t3.avatar,t3.age,t3.gender,t3.identifytype,t3.diabetes_type,t3.hyp_type,t3.apo_type,t3.is_risk,t3.sign_status,\n" +
                " CASE WHEN EXISTS(SELECT * FROM app_tb_sign_user_doctor_group where user_id = t3.id and group_id in \n" +
                "   (select id from app_tb_patient_group where doctor_id = '"+doctorInfo.getId()+"'  and del_flag = '0')) THEN 1 ELSE 0 END AS group_type\n" +
                " from app_tb_report_follow t1\n" +
                " JOIN app_tb_register_info t2 on t1.registerid = t2.registerid\n" +
                " LEFT JOIN app_tb_register_address address on t1.registerid = address.registerid\n"+
                " LEFT JOIN fam_doctor_tube_sign_user t3 ON t2.personcard = t3.card_number and t3.card_type = '01'\n" +
                " where NOT EXISTS(select * from app_tb_diabetes_assessment_remind where \n" +
                "     type=2 and registerid = t1.registerid and  create_date BETWEEN t1.remind_begin_date AND t1.remind_end_date and del_flag = '0')\n" +
                " AND NOW() BETWEEN t1.remind_begin_date AND t1.remind_end_date AND t1.del_flag = '0' \n" +
                " AND t3.identifytype = '1' " +
                " AND ((t1.doctor_name = '"+doctorAccount.getName()+"' AND t1.hospital_code = '"+doctorInfo.getHospitalId()+"')\n" +
                    " or t3.sign_doctor_personcard = '"+doctorInfo.getIdcard()+"')\n" +
                " %s %s\n" +
                " order by group_type desc , t1.follow_date DESC"+
                " limit "+(pageNo-1)*pageSize+","+(pageSize+1);




        StringBuffer buffer = new StringBuffer();
        if(null != diseaseType && !StringUtils.isEmpty(diseaseType)){
            buffer.append(" and (  ");
            StringBuffer child = new StringBuffer();
            if(diseaseType.contains("1")) child.append(" and (t3.diabetes_type != '0' or t3.diabetes_c_type = 1)");
            if(diseaseType.contains("2")) child.append(" and (t3.hyp_type = '1' or t3.hyp_c_type = 1)");
            if(diseaseType.contains("3")) child.append(" and (t3.apo_type = '1' or t3.apo_c_type = 1)");
            buffer.append(child.toString().replaceFirst("and",""));
            buffer.append(" ) ");
        }


        sql = String.format(sql,null == signStatus?"": " and sign_status = " + signStatus,buffer.toString());
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> findMineFollow(Integer pageNo, int pageSize, DoctorInfo doctorInfo, DoctorAccount doctorAccount) {
        String sql = "select t1.report_date,t2.name,t2.registerid," +
                "t3.name,t3.avatar,t3.age,t3.gender,t3.identifytype,t3.diabetes_type,t3.hyp_type,t3.apo_type,t3.is_risk,t3.sign_status\n" +
                " from app_tb_report_follow t1\n" +
                " JOIN app_tb_register_info t2 on t1.registerid = t2.registerid\n" +
                " LEFT JOIN fam_doctor_tube_sign_user t3 ON t2.personcard = t3.card_number and t3.card_type = '01'\n" +
                " where  t1.del_flag = '0' AND t1.doctor_name = '"+doctorAccount.getName()+"' AND t1.hospital_code = '"+doctorInfo.getHospitalId()+"'\n" +
                " and t3.card_type = '01' and t3.tube_type != '1' and (t3.tube_doctor_personcard = '"+doctorInfo.getIdcard()+"' or  t3.sign_doctor_personcard = '"+doctorInfo.getIdcard()+"')\n" +
                " order by  t1.report_date DESC"+
                " limit "+(pageNo-1)*pageSize+","+(pageSize+1);

        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public Integer getRemindCount(String doctorId) {
        return remindRepo.getRemindCountByType(doctorId,2);
    }

    @Override
    public Boolean hasToRemindFollow(DoctorInfo doctorInfo,DoctorAccount doctorAccount) {
        List<Map<String, Object>> list = findFollow(1, 1, null, null, doctorInfo,doctorAccount);
        if(list!=null && list.size()>0){
            return true;
        }
        return false;
    }
}
