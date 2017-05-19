package com.wondersgroup.healthcloud.services.homeservice.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeTabServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeUserServiceEntity;
import com.wondersgroup.healthcloud.jpa.repository.homeservice.HomeServiceRepository;
import com.wondersgroup.healthcloud.jpa.repository.homeservice.HomeTabServiceRepository;
import com.wondersgroup.healthcloud.jpa.repository.homeservice.HomeUserServiceRepository;
import com.wondersgroup.healthcloud.services.homeservice.HomeServices;
import com.wondersgroup.healthcloud.services.homeservice.dto.HomeServiceDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
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
    private HomeTabServiceRepository homeTabServiceRepository;

    @Autowired
    private HomeServiceRepository homeServiceRepository;

    @Autowired
    private HomeUserServiceRepository homeUserServiceRepository;

    @Override
    public HomeTabServiceEntity saveHomeTabService(HomeTabServiceEntity entity) {
        entity.setId(IdGen.uuid());
        return homeTabServiceRepository.save(entity);
    }

    @Override
    public boolean updateHomeTabService(final HomeTabServiceEntity entity) {
        final String sql = "update app_tb_tabservice set img_url = ? ,hoplink = ?,tab_type = ?,del_flag = ?,sort = ?,update_time = ?,version = ?  where id = ?";
        int count = jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, entity.getImgUrl());
                ps.setString(2, entity.getHoplink());
                ps.setInt(3, entity.getTabType());
                ps.setString(4, StringUtils.isNotBlank(entity.getDelFlag()) ? entity.getDelFlag() : "0");
                ps.setInt(5, entity.getSort());
                ps.setDate(6, new java.sql.Date(System.currentTimeMillis()));
                ps.setString(7, entity.getVersion());
                ps.setString(8, entity.getId());
                ps.execute();
            }
        });
        return count > 0 ? true : false;
    }

    @Override
    public List<String> findAllHomeTabServiceVersions(String version) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select * from (select a.version as version from app_tb_tabservice a where a.del_flag = '0' group by a.version) as b  ");
        if (StringUtils.isNotBlank(version)) {
            sql.append(" where b.version = '" + version + "'");
        }

        sql.append(" order by b.version");

        List<String> list = jdbcTemplate.query(sql.toString(), new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                String version = rs.getString("version");
                return version;
            }
        });
        return list;
    }

    @Override
    public boolean deleteHomeTableServiceByVersion(final String version) {
        String sql  = " update app_tb_tabservice set del_flag = '1',update_time = ? where version = ? ";
        int count = jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setDate(1,new java.sql.Date(System.currentTimeMillis()));
                ps.setString(2,version);
                ps.execute();
            }
        });
        return count > 0 ? true : false;
    }

    @Override
    public List<HomeTabServiceEntity> findMyHomeTabService(Map paramMap) {

        StringBuffer sql = new StringBuffer();

        String version = (null == paramMap.get("version") ? null : String.valueOf(paramMap.get("version")));


        sql.append("select * from app_tb_tabservice where del_flag = '0'  ");

        if (StringUtils.isNotBlank(version)) {
            sql.append(" and version = '" + version.trim() + "'");
        }

        sql.append(" order by sort desc ");

        List<HomeTabServiceEntity> list = jdbcTemplate.query(sql.toString(), new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                HomeTabServiceEntity entity = new HomeTabServiceEntity();
                entity.setId(rs.getString("id"));
                entity.setHoplink(rs.getString("hoplink"));
                entity.setImgUrl(rs.getString("img_url"));
                entity.setTabType(rs.getInt("tab_type"));
                entity.setVersion(rs.getString("version"));
                entity.setSort(rs.getInt("sort"));
                return entity;
            }
        });

        return list;
    }

    @Override
    public HomeServiceEntity saveHomeService(HomeServiceEntity entity) {
        entity.setId(IdGen.uuid());
        return homeServiceRepository.save(entity);
    }

    @Override
    public boolean updateHomeService(final HomeServiceEntity entity) {
        final String sql = "update app_tb_neoservice set main_title = ? ,recommend_title = ?,img_url = ? ,hoplink = ?,certified = ?,allow_close = ?,service_type = ?,del_flag = ?,sort = ?,update_time = ?,remark = ?,version = ?  where id = ?";
        int count = jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, entity.getMainTitle());
                ps.setString(2, entity.getRecommendTitle());
                ps.setString(3, entity.getImgUrl());
                ps.setString(4, entity.getHoplink());
                ps.setInt(5, entity.getCertified());
                ps.setInt(6, entity.getAllowClose());
                ps.setInt(7, entity.getServiceType());
                ps.setString(8, StringUtils.isNotBlank(entity.getDelFlag()) ? entity.getDelFlag() : "0");
                ps.setInt(9, entity.getSort());
                ps.setDate(10, new java.sql.Date(System.currentTimeMillis()));
                ps.setString(11, entity.getRemark());
                ps.setString(12, entity.getVersion());
                ps.setString(13, entity.getId());
                ps.execute();
            }
        });

        return count > 0 ? true : false;
    }

    @Override
    public HomeUserServiceEntity saveHomeUserService(HomeUserServiceEntity entity) {
        return homeUserServiceRepository.save(entity);
    }

    @Override
    public List<HomeServiceEntity> findHomeServiceByCondition(Map paramMap) {
        StringBuffer sql = new StringBuffer();
        Integer serviceType = (null == paramMap.get("serviceType") ? null : Integer.parseInt(String.valueOf(paramMap.get("serviceType"))));
        String version = (null == paramMap.get("version") ? null : String.valueOf(paramMap.get("version")));
        List<Map<String,String>> orderList = (null == paramMap.get("orderBy") ? null : ( List<Map<String,String>>)paramMap.get("orderBy"));

        Integer allowClose = ((null == paramMap.get("allowClose") ? null : Integer.parseInt(String.valueOf(paramMap.get("allowClose")))));

        sql.append("select * from app_tb_neoservice where del_flag = '0' ");

        if (null != serviceType) {
            sql.append(" and service_type = " + serviceType);
        }

        if (StringUtils.isNotBlank(version)) {
            sql.append(" and version = '" + version.trim() + "'");
        }

        if(null != allowClose){
            sql.append(" and  allow_close = "+allowClose);
        }
        if(null != orderList){
             boolean flag = true;
             for(Map<String,String> orderMap: orderList){
                 String orderBy = orderMap.get("orderBy");
                 String descOrAsc = orderMap.get("descOrAsc");
                 if(flag){
                     sql.append(" order by  ");
                     sql.append(" "+orderBy+" ").append(" "+descOrAsc+" ");
                     flag = false;
                 }else{
                     sql.append(" ,"+orderBy+" ").append(" "+descOrAsc+" ");
                 }
             }

        }else{
            sql.append(" order by sort desc,update_time desc  ");
        }


        List<HomeServiceEntity> list = jdbcTemplate.query(sql.toString(), new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                HomeServiceEntity entity = new HomeServiceEntity();
                entity.setId(rs.getString("id"));
                entity.setMainTitle(rs.getString("main_title"));
                entity.setRecommendTitle(rs.getString("recommend_title"));
                entity.setHoplink(rs.getString("hoplink"));
                entity.setImgUrl(rs.getString("img_url"));
                entity.setCertified(rs.getInt("certified"));
                entity.setServiceType(rs.getInt("service_type"));
                entity.setVersion(rs.getString("version"));
                entity.setRemark(rs.getString("remark"));
                entity.setAllowClose(rs.getInt("allow_close"));
                entity.setSort(rs.getInt("sort"));
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

        if (StringUtils.isNotBlank(registerId)) {
            sql.append(" and register_id = '" + registerId + "' ");
        }

        sql.append(" order by sort desc,update_time desc ");

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
    public List<HomeServiceEntity> findMyHomeServices(Map paramMap) {
        StringBuffer sql = new StringBuffer();
        String version = (null == paramMap.get("version") ? null : String.valueOf(paramMap.get("version")));
        String registerId = (null == paramMap.get("registerId") ? null : String.valueOf(paramMap.get("registerId")));

        if (StringUtils.isBlank(version) || StringUtils.isBlank(registerId)) {
            return Collections.EMPTY_LIST;
        }

        sql.append("select * from (select b.*,a.sort as bsort from app_tb_user_service a JOIN app_tb_neoservice b on a.service_id = b.id ");
        sql.append(" where register_id = '" + registerId + "' and b.version = '" + version + "' and b.del_flag = '0' and b.allow_close = 0 and a.del_flag = '0' ) as c order by c.bsort asc");

        List<HomeServiceEntity> list = jdbcTemplate.query(sql.toString(), new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                HomeServiceEntity entity = new HomeServiceEntity();
                entity.setId(rs.getString("id"));
                entity.setMainTitle(rs.getString("main_title"));
                entity.setRecommendTitle(rs.getString("recommend_title"));
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

    @Transactional
    @Override
    public void editMyService(List<HomeServiceEntity> oldServices, List<HomeServiceEntity> newServices, String userId) {
        if (!CollectionUtils.isEmpty(oldServices)) {
            String inSql = buildSql(oldServices);
            final String deleteSql = "update app_tb_user_service set del_flag = '1',update_time = now() where register_id = '" + userId + "' and  service_id in " + inSql + " and del_flag = '0' ";
            int count = jdbcTemplate.update(deleteSql);
        }

        int sort = 0;
        for (HomeServiceEntity dto : newServices) {
            HomeUserServiceEntity entity = new HomeUserServiceEntity();
            entity.setId(IdGen.uuid());
            entity.setRegisterId(userId);
            entity.setServiceId(dto.getId());
            entity.setDelFlag("0");
            entity.setSort(++sort);
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            homeUserServiceRepository.save(entity);
        }

    }

    @Override
    public List<String> findAllVersions(String version) {

        StringBuffer sql = new StringBuffer();
        sql.append(" select * from (select a.version as version from app_tb_neoservice a where a.del_flag = '0' group by a.version) as b  ");
        if (StringUtils.isNotBlank(version)) {
            sql.append(" where b.version = '" + version + "'");
        }

        sql.append(" order by b.version");

        List<String> list = jdbcTemplate.query(sql.toString(), new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                String version = rs.getString("version");
                return version;
            }
        });
        return list;
    }

    @Override
    public List<HomeServiceDTO> findMyBaseHomeService(Map paramMap) {
        StringBuffer sql = new StringBuffer();
        String version = (null == paramMap.get("version") ? null : String.valueOf(paramMap.get("version")));
        String registerId = (null == paramMap.get("registerId") ? null : String.valueOf(paramMap.get("registerId")));
        if (StringUtils.isBlank(version) || StringUtils.isBlank(registerId)) {
            return Collections.EMPTY_LIST;
        }

        sql.append("select a.*,(select count(1) from app_tb_user_service  as b where b.service_id = a.id and del_flag = '0' and register_id = '"+registerId+"' ) as isAdd from app_tb_neoservice as a where service_type = 1 and del_flag = '0' and allow_close = 0 and a.version = '"+version+"' ORDER BY a.sort desc ,update_time desc");

        List<HomeServiceDTO> list = jdbcTemplate.query(sql.toString(), new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                HomeServiceDTO entity = new HomeServiceDTO();
                entity.setId(rs.getString("id"));
                entity.setMainTitle(rs.getString("main_title"));
                entity.setRecommendTitle(rs.getString("recommend_title"));
                entity.setHoplink(rs.getString("hoplink"));
                entity.setImgUrl(rs.getString("img_url"));
                entity.setCertified(rs.getInt("certified"));
                entity.setServiceType(rs.getInt("service_type"));
                entity.setIsAdd(rs.getInt("isAdd"));
                return entity;
            }
        });

        return list;
    }


    String buildSql(List<HomeServiceEntity> oldServices) {
        StringBuffer sql = new StringBuffer(" ( ");
        boolean flag = true;
        for (HomeServiceEntity entity : oldServices) {
            if (flag) {
                sql.append(" '" + entity.getId() + "' ");
                flag = false;
            } else {
                sql.append(" , ").append(" '" + entity.getId() + "' ");
            }
        }
        sql.append(" ) ");
        return sql.toString();
    }
}
