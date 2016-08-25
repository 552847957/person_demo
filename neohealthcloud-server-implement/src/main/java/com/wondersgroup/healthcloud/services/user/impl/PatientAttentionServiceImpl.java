package com.wondersgroup.healthcloud.services.user.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.user.patientAttention.PatientAttention;
import com.wondersgroup.healthcloud.jpa.repository.user.doctor.PatientAttentionRepository;
import com.wondersgroup.healthcloud.services.user.PatientAttentionService;
import com.wondersgroup.healthcloud.services.user.exception.ErrorPatientAttentionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/17.
 */
@Service
public class PatientAttentionServiceImpl implements PatientAttentionService {

    @Autowired
    private PatientAttentionRepository patientAttentionRepository;

    @Autowired
    private JdbcTemplate jt;

    @Override
    public Boolean doAttention(String uid, String doctorid) {
        PatientAttention check = patientAttentionRepository.findAttentionByUidAndDoctorId(uid, doctorid);
        if (check != null) {
            throw new ErrorPatientAttentionException(String.format("用户[%s]未关注用户[%s], 所以不能取消关注", uid, doctorid));
        }
        PatientAttention attention = new PatientAttention();
        attention.setId(IdGen.uuid());
        attention.setRegisterId(uid);
        attention.setAttentionId(doctorid);
        attention.setDelFlag("0");
        attention.setAttentionStarttime(new Date());
        attention.setCreateDate(new Date());
        attention.setUpdateDate(new Date());
        patientAttentionRepository.save(attention);
        return true;

    }

    @Override
    public Boolean delAttention(String uid, String doctorid) {
        PatientAttention attention = patientAttentionRepository.findAttentionByUidAndDoctorId(uid, doctorid);
        if (attention == null) {
            throw new ErrorPatientAttentionException(String.format("用户[%s]未关注用户[%s], 所以不能取消关注", uid, doctorid));
        }
        patientAttentionRepository.delete(attention);
        return true;
    }

    @Override
    public List<Map<String,Object>> findAttentionDoctorList(String uid, int pageSize, Integer flag) {
        String query = "select a.id,a.`name` ,a.mobile,a.nickname ,a.login_name as 'loginName',a.avatar , " +
                " a.talkid ,a.talkpwd ,a.talkgroupid,i.`no`, "+
                " i.actcode,i.expertin,i.introduction,i.idcard,i.gender,i.hospital_id as 'hospitalId', " +
                " d.duty_name as 'dutyName',gb.`name` as 'departName',hi.hospital_name as 'hospitalName',pa.attention_id as 'attentionId' " +
                " from doctor_account_tb a " +
                " left join doctor_info_tb i on a.id = i.id " +
                " left join t_dic_duty d on i.duty_id = d.duty_id " +
                " left join t_dic_depart_gb gb on i.depart_standard = gb.id " +
                " left join t_dic_hospital_info hi on i.hospital_id = hi.hospital_id " +
                " left join app_tb_patient_attention pa on a.id = pa.attention_id ";

        String sql =query +
                " where pa.register_id = '%s' order by pa.attention_starttime desc limit "+(flag-1)*pageSize+","+pageSize;
        sql = String.format(sql,uid);
        return jt.queryForList(sql);
    }
}
