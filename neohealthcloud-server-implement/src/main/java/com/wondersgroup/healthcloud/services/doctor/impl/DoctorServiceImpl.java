package com.wondersgroup.healthcloud.services.doctor.impl;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.faq.FaqRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/1.
 */
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private JdbcTemplate jt;

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;


    private String query = "select a.id,a.`name` ,a.mobile,a.nickname ,a.login_name as 'loginName',a.avatar , " +
                          " a.talkid ,a.talkpwd ,a.talkgroupid,i.`no`, a.is_available as 'isAvailable',i.depart_standard as 'departStandard',  "+
                          " i.actcode,i.expertin,i.introduction,i.idcard,i.gender,i.hospital_id as 'hospitalId', " +
                          " d.duty_name as 'dutyName',gb.`name` as 'departName',hi.hospital_name as 'hospitalName' " +
                          " from doctor_account_tb a " +
                          " left join doctor_info_tb i on a.id = i.id " +
                          " left join t_dic_duty d on i.duty_id = d.duty_id " +
                          " left join t_dic_depart_gb gb on i.depart_standard = gb.id " +
                          " left join t_dic_hospital_info hi on i.hospital_id = hi.hospital_id ";

    /**
     * 根据医生uid查询医生信息
     * @param
     * @return
     */
    @Override
    public Map<String, Object> findDoctorInfoByUid(String uid) {
        String sql =query +
                " where a.id = '%s'";
        sql = String.format(sql,uid);
        try {
            return jt.queryForMap(sql);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public Doctor findDoctorByUid(String uid) {
        Map<String,Object> doctorInfo = findDoctorInfoByUid(uid);
        return new Doctor(doctorInfo);
    }

    @Override
    public List<Doctor> findDoctorByIds(String ids) {
        if(StringUtils.isBlank(ids)){
            return null;
        }
        String[] doctorIds = ids.split(",");

        StringBuffer sb = new StringBuffer();
        for(String str : doctorIds){
            sb.append(",'"+str+"'");
        }
        String param = sb.toString();

        String sql =query +
                " where a.id in  (%s) ";
        sql = String.format(sql,param.substring(1));
        RowMapper<Doctor> rowMapper = new DoctorListRowMapper();
        List<Doctor> doctors = jt.query(sql, rowMapper);
        return doctors;
    }

    public class DoctorListRowMapper implements RowMapper<Doctor> {
        @Override
        public Doctor mapRow(ResultSet rs, int i) throws SQLException {
            Doctor doctor = new Doctor();
            doctor.setUid(rs.getString("id"));
            doctor.setName(rs.getString("name"));

            doctor.setMobile(rs.getString("mobile"));
            doctor.setNickname(rs.getString("nickname"));
            doctor.setLoginName(rs.getString("loginName"));

            doctor.setIdcard(rs.getString("idcard"));
            doctor.setGender(rs.getString("gender"));

            doctor.setHospitalId(rs.getString("hospitalId"));
            doctor.setHospitalName(rs.getString("hospitalName"));
            doctor.setDutyName(rs.getString("dutyName"));
            doctor.setDepartName(rs.getString("departName"));
            doctor.setNo(rs.getString("no"));

            doctor.setIntroduction(rs.getString("introduction"));
            doctor.setExpertin(rs.getString("expertin"));
            doctor.setAvatar(rs.getString("avatar"));


            doctor.setTalkid(rs.getString("talkid"));
            doctor.setTalkpwd(rs.getString("talkpwd"));
            doctor.setTalkgroupid(rs.getString("talkgroupid"));

            doctor.setActcode(rs.getString("actcode"));

            doctor.setIsAvailable(rs.getString("isAvailable"));
            return doctor;
        }
    }

    @Override
    public Map<String, Object> findDoctorInfoByIdcard(String doctorIdcard) {
        String sql =query +
                " where i.idcard = '%s' and a.del_flag = '0' and a.is_available = '0' ";
        sql = String.format(sql,doctorIdcard);
        try {
            return jt.queryForMap(sql);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findDoctorServicesById(String uid) {

        String sql = " select sd.id,sd.icon ,sd.`name`,sd.keyword,sd.subtitle,sd.url " +
                " from doctor_account_tb a " +
                " inner join doctor_service_tb s on a.id = s.doctor_id " +
                " inner join doctor_service_dic sd on s.service_id = sd.id " +
                " where sd.is_available = '0' and a.id = '%s' order by sd.update_date ASC , sd.id asc";

        sql =  String.format(sql,uid);
        return jt.queryForList(sql);
    }

    @Override
    public Boolean checkDoctorHasService(String doctorId, String keyword) {

        String sql = " select s.doctor_id,d.keyword from doctor_service_tb s " +
                " left join doctor_service_dic d on s.service_id = d.id " +
                " where s.del_flag = '0' and d.del_flag = '0'  and s.doctor_id = '%s' and d.keyword = '%s' ";

        sql =  String.format(sql,doctorId,keyword);

        Map<String,Object> service = new HashMap<>();
        try {
            service = jt.queryForMap(sql);
        }catch (EmptyResultDataAccessException e){
            return false;
        }
        if(service!=null){
            return true;
        }
        return false;
    }


    /**
     * 根据医生uid查询医生信息
     * @param
     * @return
     */
    @Override
    public Map<String, Object> findDoctorInfoByUidAndDoctorId(String uid,String doctorId) {
        String query = "select a.id,a.`name` ,a.mobile,a.nickname ,a.login_name as 'loginName',a.avatar , " +
                " a.talkid ,a.talkpwd ,a.talkgroupid,i.`no`, "+
                " i.actcode,i.expertin,i.introduction,i.idcard,i.gender,i.hospital_id as 'hospitalId', " +
                " d.duty_name as 'dutyName',gb.`name` as 'departName',hi.hospital_name as 'hospitalName',pa.attention_id as 'attentionId' " +
                " from doctor_account_tb a " +
                " left join doctor_info_tb i on a.id = i.id " +
                " left join t_dic_duty d on i.duty_id = d.duty_id " +
                " left join t_dic_depart_gb gb on i.depart_standard = gb.id " +
                " left join t_dic_hospital_info hi on i.hospital_id = hi.hospital_id " +
                " left join app_tb_patient_attention pa on a.id = pa.attention_id and pa.register_id = '%s' ";

        String sql =query +
                " where a.id = '%s'";
        sql = String.format(sql,uid,doctorId);

        try {
            return jt.queryForMap(sql);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    /**
     * 根据医生推广邀请码查询医生信息
     * @param actcode
     * @return
     */
    @Override
    public Doctor findDoctorInfoByActcode(String actcode) {
        String sql =query +
                " where i.actcode = '%s'";
        sql = String.format(sql,actcode);
        Map<String,Object> doctorInfo = new HashMap<>();
        try {
            doctorInfo =  jt.queryForMap(sql);
        }catch (EmptyResultDataAccessException e){
                return null;
        }
        return  new Doctor(doctorInfo);

    }

    @Override
    public DoctorInfo updateIntro(String uid, String intro) {
        DoctorInfo doctorInfo = doctorInfoRepository.findOne(uid);
        if(doctorInfo == null){
            throw new ErrorDoctorAccountException("用户不存在");
        }
        doctorInfo.setIntroduction(intro);
        doctorInfo.setUpdateDate(new Date());
        doctorInfo.setUpdateBy(uid);
        return doctorInfoRepository.saveAndFlush(doctorInfo);
    }


    @Override
    public DoctorInfo updateExpertin(String uid, String expertin) {
        DoctorInfo doctorInfo = doctorInfoRepository.findOne(uid);
        if(doctorInfo == null){
            throw new ErrorDoctorAccountException("用户不存在");
        }
        doctorInfo.setExpertin(expertin);
        doctorInfo.setUpdateDate(new Date());
        doctorInfo.setUpdateBy(uid);
        return doctorInfoRepository.saveAndFlush(doctorInfo);
    }

    @Override
    public DoctorAccount updateDoctorAvatar(String uid, String avatar) {
        DoctorAccount doctorAccount = doctorAccountRepository.findOne(uid);
        if(doctorAccount == null){
            throw new ErrorDoctorAccountException("用户不存在");
        }
        doctorAccount.setAvatar(avatar);
        doctorAccount.setUpdateDate(new Date());
        doctorAccount.setUpdateBy(uid);
        return doctorAccountRepository.saveAndFlush(doctorAccount);
    }

    /**
     * 查询所有的问答的医生
     * @param kw
     * @return
     */
    @Override
    public List<Map<String, Object>> findAllFaqDoctors(String kw,String rootQid,String doctorAnswerId) {

        String sql = " select a.id , a.name from doctor_account_tb a where a.is_available = '0' ";

        if(StringUtils.isNotBlank(doctorAnswerId)){
            sql = sql + " and a.id not in ( select doctor_id from faq_question_tb where q_id = '"+rootQid+"'  and id != '"+doctorAnswerId+"' and doctor_id is not null ) ";
        }else {
            sql = sql + " and a.id not in ( select doctor_id from faq_question_tb where q_id = '"+rootQid+"' and doctor_id is not null ) ";
        }

        if(StringUtils.isNotBlank(kw)){
            sql = sql + " and a.name like '%"+kw+"%' ";
        }
        return jt.queryForList(sql);
    }


//-----医生后台





    /**
     *
     * @param pageNum
     * @param size
     * @param parameter
     * @return
     */
    @Override
    public List<Doctor> findDoctorListByPager(int pageNum, int size, Map parameter) {
        String sql =query + getWhereSqlByParameter(parameter)+" LIMIT " +(pageNum-1)*size +"," + size;
        RowMapper<Doctor> rowMapper = new DoctorListRowMapper();
        List<Doctor> doctors = jt.query(sql, rowMapper);
        return doctors;
    }

    @Override
    public int countFaqByParameter(Map parameter) {
        String sql = "select count(a.id)  "+
                " from doctor_account_tb a " +
                " left join doctor_info_tb i on a.id = i.id " +
                " left join t_dic_duty d on i.duty_id = d.duty_id " +
                " left join t_dic_depart_gb gb on i.depart_standard = gb.id " +
                " left join t_dic_hospital_info hi on i.hospital_id = hi.hospital_id "+
                getWhereSqlByParameter(parameter);
        Integer count = jt.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public List<Map<String, Object>> findDoctorServicesByIdWithoutDel(String uid) {
        String sql = " select sd.id,sd.icon ,sd.`name`,sd.keyword,sd.subtitle,sd.url " +
                " from doctor_account_tb a " +
                " inner join doctor_service_tb s on a.id = s.doctor_id " +
                " inner join doctor_service_dic sd on s.service_id = sd.id " +
                " where  a.id = '%s'";

        sql =  String.format(sql,uid);
        return jt.queryForList(sql);
    }

    @Override
    public DoctorInfo getDoctorInfoByUid(String uid) {
        DoctorInfo doctorInfo = doctorInfoRepository.findOne(uid);
        if(doctorInfo == null){
            throw new ErrorDoctorAccountException("用户不存在");
        }
        return  doctorInfo;
    }

    @Override
    public DoctorAccount getDoctorAccountByUid(String uid) {
        DoctorAccount doctorAccount = doctorAccountRepository.findOne(uid);
        if(doctorAccount == null){
            throw new ErrorDoctorAccountException("用户不存在");
        }
        return  doctorAccount;
    }

    public String getWhereSqlByParameter(Map parameter){
        StringBuffer bf = new StringBuffer();
        bf.append(" where a.del_flag = '0'  ");
        if(parameter.size()>0){
            if(parameter.containsKey("name") &&  StringUtils.isNotBlank(parameter.get("name").toString())){
                bf.append(" and a.name like '%"+parameter.get("name").toString()+"%' ");
            }
            if(parameter.containsKey("mobile") && StringUtils.isNotBlank(parameter.get("mobile").toString())){
                bf.append(" and a.mobile = "+parameter.get("mobile").toString());
            }
            if(parameter.containsKey("isAvailable") && StringUtils.isNotBlank(parameter.get("isAvailable").toString())){
                bf.append(" and a.is_available = "+parameter.get("isAvailable").toString());
            }
            if(parameter.containsKey("hospitalName") && StringUtils.isNotBlank(parameter.get("hospitalName").toString())){
                bf.append(" and hi.hospital_name like '%"+parameter.get("hospitalName").toString() +"%' ");
            }
        }
        return bf.toString();
    }


}
