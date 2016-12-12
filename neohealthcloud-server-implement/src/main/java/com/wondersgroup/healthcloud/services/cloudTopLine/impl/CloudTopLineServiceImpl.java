package com.wondersgroup.healthcloud.services.cloudTopLine.impl;

import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.repository.cloudtopline.CloudTopLineRepository;
import com.wondersgroup.healthcloud.services.cloudTopLine.CloudTopLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/9.
 */
@Service("cloudTopLineService")
public class CloudTopLineServiceImpl implements CloudTopLineService {

    private static final Logger logger = LoggerFactory.getLogger("CloudTopLineServiceImpl");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CloudTopLineRepository cloudTopLineRepository;


    @Override
    public CloudTopLine saveCloudTopLine(CloudTopLine entity) {
            entity.setUpdateTime(new Date());
            entity.setCreateTime(new Date());
            entity.setDelFlag("0");
            CloudTopLine result = cloudTopLineRepository.save(entity);
            return result;
    }

    @Override
    public boolean updateCloudTopLineIconById(CloudTopLine entity) {
         if (null == entity || null == entity.getId() || entity.getId() == 0){
             logger.info("CloudTopLineServiceImpl.updateCloudTopLineIconById 参数异常 ");
             return false;
         }

        final String sql = "update app_tb_cloud_top_line set icon_url = ? ,update_time = ? where id = ?";
        return jdbcTemplate.update(sql,new Object[]{entity.getIconUrl(),new Date(),entity.getId()}) > 0 ? true:false;
    }

    @Override
    public boolean delCloudTopLineById(Integer id) {
        if (null == id || id == 0){
            logger.info("CloudTopLineServiceImpl.delCloudTopLineById 参数异常 ");
            return false;
        }

        final String sql = "update app_tb_cloud_top_line set del_flag = ? ,update_time = ? where id = ? ";
        return jdbcTemplate.update(sql,new Object[]{"1",new Date(),id}) > 0 ? true:false;
    }


    @Override
    public List<CloudTopLine> queryCloudTopLineByCondition(Map<String, Object> paramMap) {
       StringBuffer sql = new StringBuffer();

        sql.append("select * from app_tb_cloud_top_line where 1=1 ");
         if(null != paramMap && null != paramMap.get("id")){
             Integer id = Integer.parseInt(String.valueOf(paramMap.get("id")));
             sql.append(" and id = "+id);
         }

        if(null != paramMap && null != paramMap.get("name")){
            String name = String.valueOf(paramMap.get("name"));
            sql.append(" and name like '%"+name+"%' ");
        }

        if(null != paramMap && null != paramMap.get("title")){
            String title = String.valueOf(paramMap.get("title"));
            sql.append(" and title like '%"+title+"%' ");
        }

        if(null != paramMap && null != paramMap.get("type")){
            Integer type = Integer.parseInt(String.valueOf(paramMap.get("type")));
            sql.append(" and type = "+type);
        }

        if(null != paramMap && null != paramMap.get("delFlag")){
            String delFlag = String.valueOf(paramMap.get("delFlag"));
            sql.append(" and del_flag = '"+delFlag+"'");
        }

        List<CloudTopLine> list = jdbcTemplate.query(sql.toString(), new RowMapper(){
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                CloudTopLine entity = new CloudTopLine();
                entity.setId(rs.getInt("id"));
                entity.setName(rs.getString("name"));
                entity.setTitle(rs.getString("title"));
                entity.setJumpUrl(rs.getString("jump_url"));
                entity.setType(rs.getInt("type"));
                entity.setIconUrl(rs.getString("icon_url"));
                entity.setDelFlag(rs.getString("del_flag"));
                entity.setCreateTime(rs.getDate("create_time"));
                entity.setUpdateTime(rs.getDate("update_time"));
                return entity;
            }
        });


        return list;
    }


    @Override
    public boolean updateCloudTopLineById(CloudTopLine entity) {
        if (null == entity || null == entity.getId() || 0 == entity.getId()){
            logger.info("updateCloudTopLineById.updateCloudTopLineById 参数异常 ");
            return false;
        }
        final  String sql = "update app_tb_cloud_top_line set title = ? ,jump_url = ?,type = ?,update_time = ? where id = ? ";
        return jdbcTemplate.update(sql,new Object[]{entity.getTitle(),entity.getJumpUrl(),entity.getType(),new Date(),entity.getId()}) > 0 ? true:false;
    }

}
