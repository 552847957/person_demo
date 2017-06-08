package com.wondersgroup.healthcloud.services.doctor.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorTemplate;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorUsedTemplate;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorTemplateRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorUsedTemplateRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DoctorTemplateServiceImpl implements DoctorTemplateService {

    private DoctorTemplateRepository doctorTemplateRepository;
    @Autowired
    private DoctorUsedTemplateRepository doctorUsedTemplateRepository;

    @Autowired
    private JdbcTemplate template;

    @Override
    public List<DoctorTemplate> findByDoctorIdAndType(String doctorId, String type) {
        return doctorTemplateRepository.findByDoctorIdAndType(doctorId, type);
    }

    @Override
    public DoctorTemplate update(String id, String doctorId, String type, String title, String content) {
        DoctorTemplate toSave;
        if (id == null) {
            if (doctorId == null) {
                throw new RuntimeException();
            }
            toSave = new DoctorTemplate();
            toSave.setId(IdGen.uuid());
            toSave.setDoctorId(doctorId);
        } else {
            toSave = doctorTemplateRepository.findOne(id);
        }
        toSave.setTitle(title);
        toSave.setContent(content);
        toSave.setType(type);
        toSave.setUpdateTime(new Date());

        return doctorTemplateRepository.save(toSave);
    }

    @Override
    public DoctorTemplate findOne(String id) {
        return doctorTemplateRepository.findOne(id);
    }

    @Override
    public void deleteOne(String id) {
        doctorTemplateRepository.delete(id);
    }

    @Override
    public void saveTemplate(DoctorTemplate entity) {
        if (StringUtils.isBlank(entity.getId())) {
            entity.setId(IdGen.uuid());
        }
        doctorTemplateRepository.save(entity);
    }

    @Override
    public void saveDoctorUsedTemplate(DoctorUsedTemplate entity) {
        if (StringUtils.isBlank(entity.getDoctorId()) || StringUtils.isBlank(entity.getTemplateId())) {
            return;
        }
        if (StringUtils.isBlank(entity.getId())) {
            entity.setId(IdGen.uuid());
        }
        doctorUsedTemplateRepository.save(entity);
    }

    @Override
    public List<DoctorTemplate> findLastUsedTemplate(String doctorId) {
        final String sql = "select c.* from app_tb_doctor_used_template as a  " +
                "left join app_tb_doctor_template as c on a.template_id = c.id where NOT EXISTS( " +
                " select 1 from app_tb_doctor_used_template as b where b.doctor_id = a.doctor_id " +
                " and a.template_id = b.template_id " +
                " and b.create_time > a.create_time " +
                " and b.doctor_id = '"+doctorId+"' " +
                ") " +
                "and a.doctor_id = '"+doctorId+"' " +
                "ORDER BY create_time desc limit 0,3";


        List<DoctorTemplate> list = template.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                DoctorTemplate entity = new DoctorTemplate();
                entity.setId(rs.getString("id"));
                entity.setDoctorId(rs.getString("doctor_id"));
                entity.setTitle(rs.getString("title"));
                entity.setContent(rs.getString("content"));
                entity.setCreateTime(rs.getTime("create_time"));
                return entity;
            }
        });
        return list;
    }

    @Override
    public Integer findDoctorTemplateCount(String doctorId, String type) {
        Integer count  =  template.queryForObject("select count(1) from app_tb_doctor_template as a where a.doctor_id = '"+doctorId+"' and type= '"+type+"' ",Integer.class);
        return count;
    }


    @Autowired
    public void setDoctorTemplateRepository(DoctorTemplateRepository doctorTemplateRepository) {
        this.doctorTemplateRepository = doctorTemplateRepository;
    }
}
