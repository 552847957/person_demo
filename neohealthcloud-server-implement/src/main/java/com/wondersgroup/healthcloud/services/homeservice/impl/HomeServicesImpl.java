package com.wondersgroup.healthcloud.services.homeservice.impl;

import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;
import com.wondersgroup.healthcloud.jpa.repository.homeservice.HomeServiceRepository;
import com.wondersgroup.healthcloud.services.homeservice.HomeServices;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/9.
 */
@Service("homeServicesImpl")
public class HomeServicesImpl implements HomeServices {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HomeServiceRepository homeServiceRepository;

    @Override
    public HomeServiceEntity saveHomeService(HomeServiceEntity entity) {
        return homeServiceRepository.save(entity);
    }

    @Override
    public List<HomeServiceEntity> findHomeServiceByCondition(Map paramMap) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from app_tb_neoservice where del_flag = '0' ");
        Integer serviceType = (null == paramMap.get("serviceType") ? null : Integer.parseInt(String.valueOf(paramMap.get("serviceType"))));
        String version = (null == paramMap.get("version") ? null : String.valueOf(paramMap.get("version")));

        if (null != serviceType) {
            sql.append(" and service_type = " + serviceType);
        }

        if (StringUtils.isNotBlank(version)) {
            sql.append(" and version = '" + version.trim() + "'");
        }

        List<HomeServiceEntity> list = jdbcTemplate.query(sql.toString(), new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                HomeServiceEntity entity = new HomeServiceEntity();
                entity.setId(rs.getString("id"));
                entity.setMainTitle(rs.getString("main_title"));
                entity.setHoplink(rs.getString("hoplink"));
                entity.setImgUrl(rs.getString("img_url"));
                entity.setCertified(rs.getInt("certified"));
                entity.setServiceType(rs.getInt("service_type"));
                entity.setVersion(rs.getString("version"));
                return entity;
            }
        });


        return list;
    }
}
