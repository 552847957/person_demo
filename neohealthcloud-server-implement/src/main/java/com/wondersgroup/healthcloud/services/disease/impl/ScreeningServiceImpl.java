package com.wondersgroup.healthcloud.services.disease.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessmentRemind;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRemindRepository;
import com.wondersgroup.healthcloud.services.disease.ScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2017/5/23.
 */
@Service("screeningService")
public class ScreeningServiceImpl implements ScreeningService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DiabetesAssessmentRemindRepository remindRepo;

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    @Value("${JOB_CONNECTION_URL}")
    private String jobClientUrl;

    /**
     * 获取筛查列表数据
     * @param pageNo
     * @param pageSize
     * @param signStatus 签约状态 1：已经签约居民，0：未签约居民，null：所有类型的居民
     * @param diseaseType 慢病类型 1：糖尿病，2：高血压，3：脑卒中，null：所有类型的居民
     * @return
     */
    @Override
    public List<Map<String, Object>> findScreening(Integer pageNo, int pageSize, Integer signStatus, Integer diseaseType, DoctorInfo doctorInfo) {

        String sql = "select t1.id,t2.registerid,t2.name,t2.gender,t2.identifytype,t2.headphoto,t3.diabetes_type,t3.hyp_type,t3.apo_type,\n" +
                " CASE WHEN EXISTS(SELECT * FROM app_tb_sign_user_doctor_group where user_id = t2.registerid and group_id in \n" +
                " (select id from app_tb_patient_group where doctor_id = '"+doctorInfo.getId()+"'  and del_flag = '0')) THEN 1 ELSE 0 END AS group_type\n" +
                " from (select * from app_tb_patient_assessment where del_flag = '0' and create_date >= DATE_ADD(NOW(),INTERVAL -3 MONTH) order by create_date desc)t1 \n" +
                " JOIN app_tb_register_info t2 on t1.uid = t2.registerid\n" +
                " JOIN fam_doctor_tube_sign_user t3 ON t2.personcard = t3.card_number and t3.card_type = '01'"+
                " where NOT EXISTS(select * from app_tb_diabetes_assessment_remind where \n" +
                "       type=1 and registerid = t1.uid and  DATEDIFF(create_date,t1.create_date) >= 0 and del_flag = '0')\n" +
                " and t3.is_risk = 1 and t3.tube_type != '1' and (t3.tube_doctor_personcard = '"+doctorInfo.getIdcard()+"' or  t3.sign_doctor_personcard = '"+doctorInfo.getIdcard()+"') " +
                " %s %s\n" +
                " GROUP BY t1.uid\n" +
                " order by group_type desc , t1.create_date DESC" +
                " limit "+(pageNo-1)*pageSize+","+(pageSize+1);
        sql = String.format(sql,null == signStatus?"": " and sign_status = " + signStatus,
                null == diseaseType ?"":(1 == diseaseType? " and diabetes_type != 0": (2 == diseaseType?" and hyp_type = 1":" and apo_type = 1")));
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public Boolean remind(List<String> registerIds, String doctorId,Integer type) {
        for(String registerid : registerIds){
            DiabetesAssessmentRemind remind = new DiabetesAssessmentRemind();
            remind.setId(IdGen.uuid());
            remind.setRegisterid(registerid);
            remind.setDoctorId(doctorId);
            remind.setCreateDate(new Date());
            remind.setUpdateDate(new Date());
            remind.setDelFlag("0");
            remindRepo.save(remind);

            String param = "{\"notifierUID\":\""+doctorId+"\",\"receiverUID\":\""+registerid+"\",\"msgType\":\"1\",\"msgTitle\":\"筛查提醒\",\"msgContent\":\"您的风险评估结果存在异常，建议您到所属社区卫生服务中心进行糖尿病高危筛查。\"}";
            Request build= new RequestBuilder().post().url(jobClientUrl+"/api/disease/message").body(param).build();
            JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(build).run().as(JsonNodeResponseWrapper.class);
            JsonNode result = response.convertBody();

        }
        return true;
    }
}
