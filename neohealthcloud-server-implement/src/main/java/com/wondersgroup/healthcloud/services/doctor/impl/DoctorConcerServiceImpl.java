package com.wondersgroup.healthcloud.services.doctor.impl;

import com.google.common.collect.Lists;
import com.qiniu.util.StringUtils;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorDepartment;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorDepartmentRela;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorDepartmentRelaRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorConcerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by longshasha on 16/8/31.
 */
@Service
public class DoctorConcerServiceImpl implements DoctorConcerService {

    @Autowired
    private DoctorDepartmentRelaRepository doctorDepartmentRelaRepository;

    @Autowired
    private JdbcTemplate jt;



    @Override
    @Transactional
    public Boolean updateDoctorConcerDepartment(String doctorId, String departmentIds) {
        if(!StringUtils.isNullOrEmpty(departmentIds)&&!StringUtils.isNullOrEmpty(doctorId)){
            doctorDepartmentRelaRepository.deleteById(doctorId);//先删除医生和科室的关系,在维护新的关系
            String[] ids = departmentIds.split(",");
            List<DoctorDepartmentRela> relations = Lists.newArrayList();
            for(String id:ids){
                DoctorDepartmentRela rela = new DoctorDepartmentRela();
                rela.setId(IdGen.uuid());
                rela.setDoctorid(doctorId);
                rela.setDepartid(id);
                rela.setCreateBy(doctorId);
                rela.setCreateDate(new Date());
                rela.setUpdateBy(doctorId);
                rela.setDelFlag("0");
                rela.setUpdateDate(new Date());
                relations.add(rela);
            }
            doctorDepartmentRelaRepository.save(relations);
            return true;
        }
        return false;
    }

    @Override
    public List<DoctorDepartment> queryDoctorDepartmentsByDoctorId(String doctorId) {

        String sql = " select b.id,b.name,b.pid,b.sort,b.isview,b.del_flag as 'delFlag', " +
                " b.create_by as 'createBy' ,b.create_date as 'createDate',b.update_by as 'updateBy' ," +
                " b.update_date as 'updateDate',b.source_id as 'sourceId' " +
                " from   app_tb_doctor_departmentcare a LEFT JOIN " +
                " app_dic_doctor_department b ON " +
                " a.departid = b.id AND " +
                " a.doctorid = '%s' " +
                " where  b.del_flag = 0 order by sort ";

        sql = String.format(sql,doctorId);

        RowMapper<DoctorDepartment> hospitalRowMapper = new DoctorDepartmentRowMapper();
        List<DoctorDepartment> list = jt.query(sql,hospitalRowMapper);
        return list;
    }

    public class DoctorDepartmentRowMapper implements RowMapper<DoctorDepartment> {
        @Override
        public DoctorDepartment mapRow(ResultSet rs, int i) throws SQLException {
            DoctorDepartment department = new DoctorDepartment();
            department.setId(rs.getString("id"));
            department.setName(rs.getString("name"));
            department.setPid(rs.getString("pid"));
            department.setSort(rs.getString("sort"));
            department.setIsview(rs.getString("isview"));
            department.setDelFlag(rs.getString("delFlag"));
            return department;
        }
    }


}
