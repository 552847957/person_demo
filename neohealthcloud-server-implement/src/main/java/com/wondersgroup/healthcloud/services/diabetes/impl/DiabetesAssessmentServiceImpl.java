package com.wondersgroup.healthcloud.services.diabetes.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRepository;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import com.wondersgroup.healthcloud.services.diabetes.dto.DiabetesAssessmentDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/12/6.
 */
@Service("diabetesAssessmentService")
public class DiabetesAssessmentServiceImpl implements DiabetesAssessmentService{
    @Autowired
    private DiabetesAssessmentRepository assessmentRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 患病风险评估
     * @param assessment
     * @return
     */
    @Override
    public Integer sicken(DiabetesAssessment assessment) {
        assessment.setId(IdGen.uuid());
        assessment.setHasRemind(0);
        assessment.setType(1);
        assessment.setResult(this.sickenAssement(assessment));
        assessment.setCreateDate(new Date());
        assessment.setUpdateDate(new Date());
        assessment.setDelFlag("0");
        assessmentRepo.save(assessment);
        return assessment.getResult();
    }

    /**
     * 肾病风险评估
     * @param assessment
     * @return
     */
    @Override
    public Integer kidney(DiabetesAssessment assessment) {
        int total = assessment.getIsHistory() + assessment.getIsEyeHistory() + assessment.getIsPressureHistory()
                + assessment.getIsUrine() + assessment.getIsEdema() + assessment.getIsTired() + assessment.getIsCramp();
        switch (total){
            case 0 :
                assessment.setResult(0);
                break;
            case 1:
                assessment.setResult(1);
                break;
            case 2:
                assessment.setResult(1);
                break;
            default:
                assessment.setResult(2);
        }
        assessment.setId(IdGen.uuid());
        assessment.setType(2);
        assessment.setCreateDate(new Date());
        assessment.setUpdateDate(new Date());
        assessment.setDelFlag("0");
        assessmentRepo.save(assessment);
        return assessment.getResult();
    }

    /**
     * 眼病风险评估
     * @param assessment
     * @return
     */
    @Override
    public Integer eye(DiabetesAssessment assessment) {
        assessment.setId(IdGen.uuid());
        assessment.setType(3);
        assessment.setCreateDate(new Date());
        assessment.setUpdateDate(new Date());
        assessment.setDelFlag("0");
        if(1 == assessment.getIsEyeSight() || 1 == assessment.getIsEyeFuzzy() || 1 == assessment.getIsEyeShadow()
                || 1 == assessment.getIsEyeGhosting() || 1 == assessment.getIsEyeFlash()){
            assessment.setResult(1);
        }else{
            assessment.setResult(0);
        }
        assessmentRepo.save(assessment);
        return assessment.getResult();
    }

    /**
     * 足部风险评估
     * @param assessment
     * @return
     */
    @Override
    public Integer foot(DiabetesAssessment assessment) {
        assessment.setId(IdGen.uuid());
        assessment.setType(4);
        assessment.setCreateDate(new Date());
        assessment.setUpdateDate(new Date());
        assessment.setDelFlag("0");
        int total = assessment.getIsSmoking() + assessment.getIsEyeProblem() + assessment.getIsKidney()
                + assessment.getIsCardiovascular() + assessment.getIsLimbsEdema() + assessment.getIsLimbsTemp()
                + assessment.getIsDeformity() + assessment.getIsFootBeat() + assessment.getIsShinBeat();
        if(assessment.getHbac() <= 7 && 0 ==total){
            assessment.setResult(0);
        }
        if(assessment.getHbac() > 7 && 0 ==total){
            assessment.setResult(1);
        }
        if(assessment.getHbac() > 7 && 0 !=total){
            assessment.setResult(2);
        }
        assessmentRepo.save(assessment);
        return assessment.getResult();
    }

    @Override
    public List<DiabetesAssessmentDTO> findAssessment(Integer pageNo, Integer pageSize, Map param) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("select t1.id, t1.age, t1.gender, t1.height, " +
                " t1.weight, t1.waist, t1.isIGR, t1.isSit, t1.isFamily, t1.isLargeBaby, t1.isHighPressure, t1.isBloodFat, " +
                " t1.isArteriesHarden, t1.isSterol, t1.isPCOS, t1.isMedicineTreat," +
                " t1.create_date,t2.name from app_tb_diabetes_assessment t1 ");
        buffer.append(" join app_tb_register_info t2 on t1.registerid = t2.registerid");
        buffer.append(" where  t1.type = 1 and t1.result = 1 and t1.hasRemind = 0\n" );
        buffer.append(" and t1.id = ( select id from app_tb_diabetes_assessment " +
                "               where registerid = t1.registerid and hasRemind = 0 and type = 1 and result = 1 order by create_date desc LIMIT 1)");
        if(param.containsKey("name") &&  null != param.get("name") && !StringUtils.isEmpty(param.get("name").toString())){
            buffer.append(" and t2.name like '%"+ param.get("name")+"%'");
        }
        buffer.append(" and t1.del_flag = '0'");
        buffer.append(" order by t1.create_date desc");
        buffer.append(" limit "+(pageNo-1)*pageSize+","+pageSize);
//        return jdbcTemplate.queryForList(buffer.toString(),DiabetesAssessmentDTO.class);
        return jdbcTemplate.query(buffer.toString(), new RowMapper<DiabetesAssessmentDTO>() {
            @Override
            public DiabetesAssessmentDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                DiabetesAssessmentDTO dto = new DiabetesAssessmentDTO();
                dto.setId(resultSet.getString("id"));
                dto.setAge(resultSet.getInt("age"));
                dto.setGender(resultSet.getInt("gender"));
                dto.setHeight(resultSet.getDouble("height"));
                dto.setWeight(resultSet.getDouble("weight"));
                dto.setWaist(resultSet.getDouble("waist"));
                dto.setIsIGR(resultSet.getInt("isIGR"));
                dto.setIsSit(resultSet.getInt("isSit"));
                dto.setIsFamily(resultSet.getInt("isFamily"));
                dto.setIsLargeBaby(resultSet.getInt("isLargeBaby"));
                dto.setIsHighPressure(resultSet.getInt("isHighPressure"));
                dto.setIsBloodFat(resultSet.getInt("isBloodFat"));
                dto.setIsArteriesHarden(resultSet.getInt("isArteriesHarden"));
                dto.setIsSterol(resultSet.getInt("isSterol"));
                dto.setIsPCOS(resultSet.getInt("isPCOS"));
                dto.setIsMedicineTreat(resultSet.getInt("isMedicineTreat"));
                dto.setCreate_date(resultSet.getDate("create_date"));
                dto.setName(resultSet.getString("name"));
                return dto;
            }
        });
    }

    @Override
    public Integer findAssessmentTotal(Map param) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("select t1.registerid from app_tb_diabetes_assessment t1 ");
        if(param.containsKey("name") &&  null != param.get("name")){
            buffer.append(" join app_tb_register_info t2 on t1.registerid = t2.registerid");
        }
        buffer.append(" where  t1.type = 1 and t1.result = 1 and t1.hasRemind = 0\n" );
        if(param.containsKey("name") &&  null != param.get("name") && !StringUtils.isEmpty(param.get("name").toString())){
            buffer.append(" and t2.name like '%"+ param.get("name")+"%'");
        }
        buffer.append(" and t1.del_flag = '0'");
        buffer.append(" GROUP BY t1.registerid");
        return jdbcTemplate.queryForList(buffer.toString()).size();
    }

    @Override
    public Boolean remind(String[] ids) {
        List<String> registerIds = assessmentRepo.findRegisterById(ids);
        assessmentRepo.updateRemindByRegister(registerIds,new Date());
        return true;
    }


    private Integer sickenAssement(DiabetesAssessment assessment){
        if(assessment.getAge() >= 40 || 1== assessment.getIsIGR()
                || 1 == assessment.getIsSit() || 1 == assessment.getIsFamily() || (2 == assessment.getGender() && 1 == assessment.getIsLargeBaby())
                || 1 == assessment.getIsHighPressure() || 1 == assessment.getIsBloodFat() || 1== assessment.getIsArteriesHarden()
                || 1 == assessment.getIsSterol() || (2 == assessment.getGender() && 1 == assessment.getIsPCOS()) || 1 == assessment.getIsMedicineTreat()){
            return 1;
        }
        if(null != assessment.getHeight() && null != assessment.getWeight()){//待定
            DecimalFormat d =new DecimalFormat("##.00");
            Double value = Double.valueOf(d.format(assessment.getWeight()/Math.pow((assessment.getHeight()/100), 2)));
            if(value >= 24 ){
                return 1;
            }
        }
        if(null != assessment.getGender() && null != assessment.getWaist()){
            if(1 == assessment.getGender() && assessment.getWaist() >= 90){
                return 1;
            }
            if(2 == assessment.getGender() && assessment.getWaist() >= 85){
                return 1;
            }
        }
        return 0;
    }
}
