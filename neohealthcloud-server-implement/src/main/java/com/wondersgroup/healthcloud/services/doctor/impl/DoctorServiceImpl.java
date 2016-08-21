package com.wondersgroup.healthcloud.services.doctor.impl;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.faq.FaqRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
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
                          " a.talkid ,a.talkpwd ,a.talkgroupid,i.`no`, "+
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
        return jt.queryForMap(sql);
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
        return jt.queryForMap(sql);
    }

    /**
     * 根据医生推广邀请码查询医生信息
     * @param actcode
     * @return
     */
    @Override
    public Map<String, Object> findDoctorInfoByActcode(String actcode) {
        String sql =query +
                " where i.actcode = '%s'";
        sql = String.format(sql,actcode);
        return jt.queryForMap(sql);
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
            sql = sql + " and a.id not in ( select doctor_id from faq_question_tb where q_id = '"+rootQid+"'  and id != '"+doctorAnswerId+"' ) ";
        }else {
            sql = sql + " and a.id not in ( select doctor_id from faq_question_tb where q_id = '"+rootQid+"' ) ";
        }

        if(StringUtils.isNotBlank(kw)){
            sql = sql + " and a.name like '%"+kw+"%' ";
        }
        return jt.queryForList(sql);
    }


}
