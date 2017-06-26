package com.wondersgroup.healthcloud.services.disease.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessmentRemind;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.ReportFollow;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.assessment.AssessmentRepository;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRemindRepository;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.ReportFollowRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportFollowDTO;
import com.wondersgroup.healthcloud.services.disease.ScreeningService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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

    @Autowired
    private DictCache dictCache;

    @Value("${JOB_CONNECTION_URL}")
    private String jobClientUrl;

    @Autowired
    private DoctorAccountRepository doctorAccountRepo;

    @Autowired
    private ReportFollowRepository reportFollowRepo;

    @Autowired
    private AssessmentRepository assessmentRepo;

    @Autowired
    private DoctorTubeSignUserRepository tubeSignUserRepo;

    @Autowired
    private RegisterInfoRepository registerInfoRepo;

    @Value("${disease.h5.url}")
    private String diseaseUrl;

    /**
     * 获取筛查列表数据
     * @param pageNo
     * @param pageSize
     * @param signStatus 签约状态 1：已经签约居民，0：未签约居民，null：所有类型的居民
     * @param diseaseType 慢病类型 1：糖尿病，2：高血压，3：脑卒中，null：所有类型的居民
     * @return
     */
    @Override
    public List<Map<String, Object>> findScreening(Integer pageNo, int pageSize, Integer signStatus, String diseaseType, DoctorInfo doctorInfo) {

        String sql = "select t1.id,t2.registerid," +
                "t3.name,t3.avatar,t3.age,t3.gender,t3.identifytype,t3.diabetes_type,t3.hyp_type,t3.apo_type,t3.sign_status," +
                " CASE WHEN EXISTS(SELECT * FROM app_tb_sign_user_doctor_group where del_flag = '0' and user_id = t3.id and group_id in \n" +
                " (select id from app_tb_patient_group where doctor_id = '"+doctorInfo.getId()+"'  and del_flag = '0')) THEN 1 ELSE 0 END AS group_type\n" +
                " from (select * from (select * from app_tb_patient_assessment where del_flag = '0' and is_oneself=1 and create_date >= DATE_ADD(NOW(),INTERVAL -3 MONTH) order by create_date desc)t0 GROUP by t0.uid)t1 \n" +
                " JOIN app_tb_register_info t2 on t1.uid = t2.registerid\n" +
                " LEFT JOIN app_tb_register_address address on t2.registerid = address.registerid\n"+
                " LEFT JOIN fam_doctor_tube_sign_user t3 ON t2.personcard = t3.card_number and t3.card_type = '01'"+
                " where t1.result is not NULL AND NOT EXISTS(select * from app_tb_diabetes_assessment_remind where \n" +
                "       type=1 and registerid = t1.uid and  create_date >= t1.create_date and del_flag = '0')\n" +
                " and t3.is_risk is not null and t3.is_risk = '1'\n"+
                " and t3.identifytype = '1' and t3.del_flag = '0'  and \n " +
                " ( (t3.sign_status ='1' AND t3.sign_doctor_personcard = '"+doctorInfo.getIdcard()+"') OR " +
                "      (t3.sign_status ='0' and t3.sign_doctor_personcard is null and t3.tube_doctor_personcard = '"+doctorInfo.getIdcard()+"') %s) " +
                " %s %s\n" +
                " order by group_type desc , t1.create_date DESC" +
                " limit "+(pageNo-1)*pageSize+","+(pageSize+1);

        StringBuffer buffer = new StringBuffer();
        if(null != diseaseType && !StringUtils.isEmpty(diseaseType)){
            buffer.append(" and ( ");
            StringBuffer child = new StringBuffer();
            if(diseaseType.contains("1")) child.append(" and t3.diabetes_c_type = 1 and (t3.diabetes_type is null or t3.diabetes_type = '0')");
            if(diseaseType.contains("2")) child.append(" and t3.hyp_c_type = 1 and (t3.hyp_type is null or t3.hyp_type = '0')");
            if(diseaseType.contains("3")) child.append(" and t3.apo_c_type = 1 and (t3.apo_type is null or t3.apo_type = '0')");
            buffer.append(child.toString().replaceFirst("and",""));
            buffer.append(" ) ");
        }

        String county = dictCache.queryHospitalAddressCounty(doctorInfo.getHospitalId());
        String area_filter = "";
        if(null != county && !StringUtils.isEmpty(county)){
            area_filter = " or (t3.sign_status ='0' AND t3.sign_doctor_personcard is null and " +
                    " (address.province = '"+county+"' or address.city = '"+county+"' or" +
                    " address.county = '"+county+"' or address.town = '"+county+"' ))\n";
        }

        sql = String.format(sql,area_filter,null == signStatus?"": " and sign_status = " + signStatus,buffer.toString());
        return jdbcTemplate.queryForList(sql);
    }

    /**
     *
     * @param registerIds
     * @param doctorId
     * @param type
     * @return 提醒类型，1：筛查提醒，2：随访提醒
     */
    @Override
    public Boolean remind(List<String> registerIds, String doctorId,Integer type) {
        DoctorAccount account = doctorAccountRepo.findOne(doctorId);
        String content = null;

        for(String registerid : registerIds){
            if(1 == type){
                Assessment assessment = assessmentRepo.getRecentRiskAssess(registerid);
                RegisterInfo registerInfo = registerInfoRepo.findOne(registerid);
                DoctorTubeSignUser signUser = tubeSignUserRepo.queryInfoByCard(registerInfo.getPersoncard());

                StringBuffer buffer = new StringBuffer();
                if(assessment.getResult().contains("1") && (null == signUser.getDiabetesType() || "0".equals(signUser.getDiabetesType())))buffer.append("糖尿病，");
                if(assessment.getResult().contains("2") && (null == signUser.getHypType() || "0".equals(signUser.getHypType())))buffer.append("高血压，");
                if(assessment.getResult().contains("3") && (null == signUser.getApoType() || "0".equals(signUser.getApoType())))buffer.append("脑卒中，");
                content = String.format("%s医生提醒您可能患有%s请尽快到所属社区卫生服务中心进行筛查。",account.getName(),
                        buffer.toString());
            }else{
                ReportFollow report = reportFollowRepo.getReport(registerid);
                content = String.format("%s医生提醒您于%s之前到所属社区卫生服务中心进行随访。",account.getName(),
                        null != report && null != report.getRemindBeginDate()?new DateTime(report.getRemindEndDate()).toString("yyyy-MM-dd"):"");
            }

            DiabetesAssessmentRemind remind = new DiabetesAssessmentRemind();
            remind.setId(IdGen.uuid());
            remind.setRegisterid(registerid);
            remind.setDoctorId(doctorId);
            remind.setCreateDate(new Date());
            remind.setUpdateDate(new Date());
            remind.setType(type);
            remind.setDelFlag("0");

            remindRepo.save(remind);

            String param = "{\"notifierUID\":\""+doctorId+"\",\"receiverUID\":\""+registerid+"\",\"msgType\":\""+type+"\"," +
                    " \"msgTitle\":\""+(type == 1?"筛查提醒":"随访提醒")+"\",\"msgContent\":\""+content+"\" %s}";
            param = String.format(param,type==2?",\"jumpUrl\":\""+diseaseUrl+"/followupplan/"+registerid+"\"":"");
            Request build= new RequestBuilder().post().url(jobClientUrl+"/api/disease/message").body(param).build();
            JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(build).run().as(JsonNodeResponseWrapper.class);
            JsonNode result = response.convertBody();

        }
        return true;
    }

    @Override
    public Integer getRemindCount(String doctorId) {
        return remindRepo.getRemindCountByType(doctorId,1);
    }

    @Override
    public Boolean hasToRemindScreened(DoctorInfo doctorInfo) {
        List<Map<String, Object>> list = findScreening(1,1, null, null, doctorInfo);
        if(list!=null && list.size()>0){
            return true;
        }
        return false;
    }
}
