package com.wondersgroup.healthcloud.services.homeservice.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeUserServiceEntity;
import com.wondersgroup.healthcloud.jpa.repository.homeservice.HomeServiceRepository;
import com.wondersgroup.healthcloud.jpa.repository.homeservice.HomeUserServiceRepository;
import com.wondersgroup.healthcloud.services.homeservice.HomeServices;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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

    @Autowired
    private HomeUserServiceRepository homeUserServiceRepository;

    @Override
    public HomeServiceEntity saveHomeService(HomeServiceEntity entity) {
        entity.setId(IdGen.uuid());
        return homeServiceRepository.save(entity);
    }

    @Override
    public HomeUserServiceEntity saveHomeUserService(HomeUserServiceEntity entity) {
        return homeUserServiceRepository.save(entity);
    }

    @Override
    public List<HomeServiceEntity> findHomeServiceByCondition(Map paramMap) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from app_tb_neoservice where del_flag = '0' ");
        Integer serviceType = (null == paramMap.get("serviceType") ? null : Integer.parseInt(String.valueOf(paramMap.get("serviceType"))));
        String version = (null == paramMap.get("version") ? null : String.valueOf(paramMap.get("version")));

        boolean baseServiceFlag = (null == paramMap.get("baseServiceFlag") ? false : Boolean.valueOf(String.valueOf(paramMap.get("baseServiceFlag"))));

        if (null != serviceType) {
            sql.append(" and service_type = " + serviceType);
        }

        if (StringUtils.isNotBlank(version)) {
            sql.append(" and version = '" + version.trim() + "'");
        }

        if(baseServiceFlag){
            List<HomeServiceEntity> baseService = (null == paramMap.get("baseServices") ? null : (List<HomeServiceEntity>)paramMap.get("baseServices"));
            if(!CollectionUtils.isEmpty(baseService)){
                String inSql = buildSql(baseService);
                sql.append(" and id  in " + inSql);
            }

        }

         sql.append(" order by sort asc ");

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

    @Override
    public List<HomeUserServiceEntity> findHomeUserServiceByCondition(Map paramMap) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from app_tb_user_service where del_flag = '0' ");

        String registerId = (null == paramMap.get("registerId") ? null : String.valueOf(paramMap.get("registerId")));

        if(StringUtils.isNotBlank(registerId)){
            sql.append(" and register_id = '"+registerId+"' ");
        }

        sql.append(" order by create_time asc ");

        List<HomeUserServiceEntity> list = jdbcTemplate.query(sql.toString(), new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                HomeUserServiceEntity entity = new HomeUserServiceEntity();
                entity.setId(rs.getString("id"));
                entity.setServiceId(rs.getString("service_id"));
                return entity;
            }
        });

        return list;
    }

    @Override
    public void editMyService(List<HomeServiceEntity> oldServices, List<HomeServiceEntity> newServices, String userId) {
        if(!CollectionUtils.isEmpty(oldServices)){
            String inSql = buildSql(oldServices);
            final String deleteSql = "update app_tb_user_service set del_flag = '1',update_time = now() where register_id = '"+userId+"' and  service_id in "+inSql+" and del_flag = '0' ";
            int count = jdbcTemplate.update(deleteSql);
        }

        for(HomeServiceEntity dto:newServices){
            HomeUserServiceEntity entity = new HomeUserServiceEntity();
            entity.setId(IdGen.uuid());
            entity.setRegisterId(userId);
            entity.setServiceId(dto.getId());
            entity.setDelFlag("0");
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            homeUserServiceRepository.save(entity);
        }

    }

    @Override
    public List<String> findAllVersions() {
        String sql = " select * from (select a.version as version from app_tb_neoservice a where a.del_flag = '0' group by a.version) as b order by b.version ";
        List<String> list = jdbcTemplate.query(sql.toString(), new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
               String version = rs.getString("version");
                return version;
            }
        });
        return list;
    }


    String buildSql(List<HomeServiceEntity> oldServices){
        StringBuffer sql = new StringBuffer(" ( ");
        boolean flag = true;
        for(HomeServiceEntity entity:oldServices){
           if(flag){
               sql.append(" '"+entity.getId()+"' ");
               flag = false;
           }else{
               sql.append(" , ").append(" '"+entity.getId()+"' ");
           }
        }
        sql.append(" ) ");
        return sql.toString();
    }
}
