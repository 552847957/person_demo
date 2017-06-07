package com.wondersgroup.healthcloud.services.interven.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.NeoFamInterventionRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInterventionRepository;
import com.wondersgroup.healthcloud.services.interven.DoctorIntervenService;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by longshasha on 17/5/18.
 */
@Service
public class DoctorIntervenServiceImpl implements DoctorIntervenService {
    private static final Logger logger = LoggerFactory.getLogger("exlog");

    @Value("${disease.h5.url}")
    private String diseaseH5Url;

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    @Value("${JOB_CONNECTION_URL}")
    private String jobClientUrl;

    @Autowired
    private DoctorInterventionRepository doctorInterventionRepository;

    @Autowired
    private NeoFamInterventionRepository neoFamInterventionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     *
     * @param uid
     * @param signStatus
     * @param interven_type 用逗号隔开 '10000|20000'
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<IntervenEntity> findTodoInterveneList(String uid, String signStatus, String interven_type, int pageNo, int pageSize) {

        String sql = " select t1.register_id,t1.typelist,t3.`name`,t3.gender,t3.age,t3.identifytype," +
                " t2.headphoto as avatar,t3.diabetes_type,t3.hyp_type,t3.apo_type,t3.is_risk,\n" +
                " CASE WHEN EXISTS(SELECT * FROM app_tb_sign_user_doctor_group where user_id = t2.registerid and group_id in \n" +
                " (select id from app_tb_patient_group where doctor_id = '%s'  and del_flag = '0')) THEN 1 ELSE 0 END AS group_type \n" +
                " from ( " +
                " select a.* from (\n" +
                " select register_id,warn_date,GROUP_CONCAT(distinct type) typelist from neo_fam_intervention \n" +
                " where type!='30000' and type!='41000' and del_flag='0' and is_deal ='0'  and warn_date>=DATE_SUB(CURDATE(),INTERVAL 90 day) group by register_id) a\n" +
                " INNER JOIN\n" +
                " (select register_id from neo_fam_intervention \n" +
                " where type!='30000' and type!='41000' and del_flag='0' and is_deal ='0'  and warn_date>=DATE_SUB(CURDATE(),INTERVAL 90 day) %s group by register_id \n" +
                " ) b on a.register_id=b.register_id " +
                ") t1\n" +
                " JOIN app_tb_register_info t2 on t1.register_id = t2.registerid \n" +
                " JOIN fam_doctor_tube_sign_user t3 ON t2.personcard = t3.card_number and t3.card_type = '01'\n" +
                " %s " +
                " order by group_type desc,t1.warn_date desc " +
                " limit "+(pageNo)*pageSize+","+(pageSize);
        StringBuffer  sb = new StringBuffer("");
        if(StringUtils.isNotBlank(interven_type)){
            sb.append("and type REGEXP '");
            String[] types = interven_type.split(",");
            for (String type : types){
                sb.append(type).append("|");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("'");
        }
        sql = String.format(sql,uid,sb.toString(),StringUtils.isBlank(signStatus)?"": " and sign_status = " + signStatus);
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper(IntervenEntity.class));
    }


    /**
     * 查询用户未干预异常血糖值
     * @param registerId
     * @param is_all
     * @param pageNo
     * @param pageSize
     * @param size
     * @return
     */
    @Override
    public List<NeoFamIntervention> findBloodGlucoseOutlierListByRegisterId(String registerId, Boolean is_all, int pageNo, int pageSize, int size) {
        String sql = " select * from neo_fam_intervention where  register_id = '%s' and (type = '10000' or type = '20000')\n" +
                     " and del_flag='0' and is_deal ='0' and warn_date>=DATE_SUB(CURDATE(),INTERVAL 90 day)" +
                     " order by warn_date desc ";
        if(is_all){
            sql = sql + " limit "+(pageNo)*pageSize+","+(pageSize);
        }else{
            sql = sql + " limit "+size;
        }
        sql = String.format(sql,registerId);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(NeoFamIntervention.class));
    }

    /**
     * 查询用户未干预异常血压值
     * @param registerId
     * @param is_all
     * @param pageNo
     * @param pageSize
     * @param size
     * @return
     */
    @Override
    public List<NeoFamIntervention> findpressureOutlierListByRegisterId(String registerId, Boolean is_all, int pageNo, int pageSize, int size) {
        String sql = " select * from neo_fam_intervention \n" +
                " where  del_flag='0' and is_deal ='0' and warn_date>=DATE_SUB(CURDATE(),INTERVAL 90 day) \n" +
                " and register_id = '%s' and type REGEXP '40000|40001|40002|40003|40004'" +
                " order by warn_date desc ";
        if(is_all){
            sql = sql + " limit "+pageNo*pageSize+","+(pageSize);
        }else{
            sql = sql + " limit "+size;
        }
        sql = String.format(sql,registerId);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(NeoFamIntervention.class));
    }

    /**
     * 根据用户registerId 查询该用户未干预的所有异常类型(汉字转换的)
     * @param registerId
     * @return
     */
    @Override
    public String findNotDealInterveneTypes(String registerId) {
        String sql = " select GROUP_CONCAT(distinct type) typelist from neo_fam_intervention \n" +
                " where type!='30000' and del_flag='0' and is_deal = '0' and warn_date>=DATE_SUB(CURDATE(),INTERVAL 90 day) \n" +
                " and  register_id = '%s'";
        sql = String.format(sql,registerId);
        String types = jdbcTemplate.queryForObject(sql,String.class);
//        String interveneTypeNames = "";
//        if(StringUtils.isNotBlank(types)){
//            interveneTypeNames = IntervenEnum.getIntervenTypeNames(types);
//        }
        return jdbcTemplate.queryForObject(sql,String.class);
    }

    /**
     * 干预保存
     * @param doctorId
     * @param patientId
     * @param content
     */
    @Override
    public void intervenSaveOrUpdate(String doctorId, String patientId, String content) {
        //医生我的干预列表添加一条数据
        try {
            String types = findNotDealInterveneTypes(patientId);
            String type = "0";//综合类型 既有血糖又有血压
            if(types.contains("10000") || types.contains("20000")){
                type = "1";//异常血糖干预
            }
            DoctorIntervention doctorIntervention = new DoctorIntervention();
            doctorIntervention.setId(IdGen.uuid());
            doctorIntervention.setDoctorId(doctorId);
            doctorIntervention.setPatientId(patientId);
            doctorIntervention.setContent(content);
            doctorIntervention.setType(type);
            doctorIntervention.setCreateTime(new Date());
            doctorIntervention.setDelFlag("0");
            doctorIntervention.setUpdateTime(new Date());
            doctorInterventionRepository.save(doctorIntervention);
            //干预异常数据表修改is_deal 和 doctor_intervention_id 值

            neoFamInterventionRepository.setInterventionIsdealAndInterventionId(patientId, doctorIntervention.getId(),new Date());

            //调用job给用户端发送消息
            String jumpUrl = diseaseH5Url + "/DoctorAdviceDetail/" +doctorIntervention.getId();
            String param = "{\"notifierUID\":\"" + doctorId + "\",\"receiverUID\":\"" + patientId + "\",\"msgType\":\"0\",\"msgTitle\":\"干预提醒\",\"msgContent\":\"近期您血糖或血压数据异常，建议您到所属社区卫生服务中心专业咨询。点击查看医生相关建议。\",\"jumpUrl\":\"" + jumpUrl + "\"}";
            Request build = new RequestBuilder().post().url(jobClientUrl + "/api/disease/message").body(param).build();
            JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(build).run().as(JsonNodeResponseWrapper.class);
            JsonNode result = response.convertBody();
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
    }

    /**
     * 医生我的干预列表
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<IntervenEntity> findPersonalInterveneList(String uid, int pageNo, int pageSize) {
        String sql = " select di.patient_id as register_id,aa.typelist,u.`name`,u.gender,u.age,\n" +
                     " u.identifytype,di.id,di.create_time asinterventionDate,di.content\n" +
                     " from app_tb_doctor_intervention di\n" +
                     " inner join \n" +
                     " (select a.* from (\n" +
                     " select register_id,doctor_intervention_id,GROUP_CONCAT(distinct type) typelist from neo_fam_intervention \n" +
                     " where type!='30000' and del_flag='0' and is_deal ='1' and doctor_intervention_id is not null  group by register_id,doctor_intervention_id) a\n" +
                     " INNER JOIN\n" +
                     " (select register_id,doctor_intervention_id from neo_fam_intervention \n" +
                     " where type!='30000' and del_flag='0' and is_deal ='1' and doctor_intervention_id is not null  group by register_id,doctor_intervention_id\n" +
                     " ) b on a.register_id=b.register_id ) aa on di.id = aa.doctor_intervention_id\n" +
                     " JOIN app_tb_register_info info on di.patient_id = info.registerid\n" +
                     " JOIN fam_doctor_tube_sign_user u on info.personcard = u.card_number and u.card_type = '01' \n" +
                     " where di.del_flag = '0' and di.doctor_id = '%s'" +
                     " order by di.create_time desc" +
                     " limit "+(pageNo)*pageSize+","+(pageSize);
        sql = String.format(sql,uid);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(IntervenEntity.class));
    }

    /**
     * 我的干预的血糖异常值
     * @param interventionId
     * @param is_all
     * @param pageNo
     * @param pageSize
     * @param size
     * @return
     */
    @Override
    public List<NeoFamIntervention> findBloodGlucoseOutlierListByInterventionId(String interventionId, Boolean is_all, int pageNo, int pageSize, int size) {
        NeoFamIntervention neoFamIntervention = neoFamInterventionRepository.findLatestByInterventionId(interventionId);
        if(neoFamIntervention == null){
            return null;
        }
        String sql = " select * from neo_fam_intervention where  register_id = '%s' and (type = '10000' or type = '20000')\n" +
                " and del_flag='0' and is_deal ='1' and create_date <= '%s')" +
                " order by warn_date desc ";
        if(is_all){
            sql = sql + " limit "+(pageNo)*pageSize+","+(pageSize);
        }else{
            sql = sql + " limit "+size;
        }
        sql = String.format(sql,neoFamIntervention.getRegisterId(), DateFormatter.dateTimeFormat(neoFamIntervention.getCreateDate()));
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(IntervenEntity.class));
    }

    /**
     * 我的干预中血压的异常值
     * @param interventionId
     * @param is_all
     * @param pageNo
     * @param pageSize
     * @param size
     * @return
     */
    @Override
    public List<NeoFamIntervention> findpressureOutlierListByInterventionId(String interventionId, Boolean is_all, int pageNo, int pageSize, int size) {
        NeoFamIntervention neoFamIntervention = neoFamInterventionRepository.findLatestByInterventionId(interventionId);
        if(neoFamIntervention == null){
            return null;
        }
        String sql = " select * from neo_fam_intervention \n" +
                " where  del_flag='0' and is_deal ='1' \n" +
                " and register_id = '%s' and type REGEXP '40000|40001|40002|40003|40004' and create_date <= '%s'" +
                " order by warn_date desc ";
        if(is_all){
            sql = sql + " limit "+(pageNo)*pageSize+","+(pageSize);
        }else{
            sql = sql + " limit "+size;
        }
        sql = String.format(sql,neoFamIntervention.getRegisterId(), DateFormatter.dateTimeFormat(neoFamIntervention.getCreateDate()));
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(NeoFamIntervention.class));
    }

    /**
     * 根据医生Id 查询异常干预 已干预条数
     * @param doctorId
     * @return
     */
    @Override
    public int countHasInterventionByDoctorId(String doctorId) {

        return doctorInterventionRepository.countHasInterventionByDoctorId(doctorId);
    }

    /**
     * 根据用户registerId 查询用户G端基本信息 和 疾病标签
     * @param registerId
     * @return
     */
    @Override
    public IntervenEntity getUserDiseaseLabelByRegisterId(String registerId) {
        String sql = " select a.registerid as register_id,b.`name`,b.gender,a.headphoto as avatar,b.age,b.identifytype,\n" +
                "       b.diabetes_type,b.hyp_type,b.apo_type,b.is_risk\n" +
                " from app_tb_register_info a \n" +
                " join fam_doctor_tube_sign_user b on a.personcard = b.card_number and b.card_type = '01'\n" +
                " where a.registerid = '%s' order by b.update_date desc \n" +
                " limit 1";
        sql = String.format(sql,registerId);
        List<IntervenEntity> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper(IntervenEntity.class));
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据患者registerId查询用户待干预异常数量
     * @param registerId
     * @return
     */
    @Override
    public Boolean hasTodoIntervensByRegisterId(String registerId) {

        int intervens = neoFamInterventionRepository.countTodoIntervensByRegisterId(registerId);
        if(intervens>0){
            return true;
        }
        return false;
    }
}
