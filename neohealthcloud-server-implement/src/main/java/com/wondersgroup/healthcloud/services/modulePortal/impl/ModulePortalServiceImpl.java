package com.wondersgroup.healthcloud.services.modulePortal.impl;

import com.wondersgroup.healthcloud.jpa.entity.moduleportal.ModulePortal;
import com.wondersgroup.healthcloud.jpa.enums.VisibleEnum;
import com.wondersgroup.healthcloud.jpa.repository.moduleportal.ModulePortalRepository;
import com.wondersgroup.healthcloud.services.modulePortal.ModulePortalService;
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
 * Created by Administrator on 2016/12/12.
 */
@Service("modulePortalService")
public class ModulePortalServiceImpl implements ModulePortalService {

    private static final Logger logger = LoggerFactory.getLogger("ModulePortalServiceImpl");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ModulePortalRepository modulePortalRepository;

    @Override
    public ModulePortal saveModulePortal(ModulePortal entity) {
        entity.setUpdateTime(new Date());
        entity.setCreateTime(new Date());
        entity.setIsVisible(VisibleEnum.VISIBLE.getId());
        entity.setDelFlag("0");
        ModulePortal result = modulePortalRepository.save(entity);
        return result;
    }

    @Override
    public List<ModulePortal> queryAllModulePortal() {
        final String sql = "select * from app_tb_module_portal where del_flag = '0' and is_visible = '"+VisibleEnum.VISIBLE.getId()+"' order by sort asc ";
        List<ModulePortal> list = jdbcTemplate.query(sql, new RowMapper(){
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                ModulePortal entity = new ModulePortal();
                entity.setItemName(rs.getString("item_name"));
                entity.setId(rs.getInt("id"));
                entity.setIconUrl(rs.getString("icon_url"));
                entity.setMainTitle(rs.getString("main_title"));
                entity.setSubTitle(rs.getString("sub_title"));
                entity.setJumpUrl(rs.getString("jump_url"));
                entity.setIsVisible(rs.getString("is_visible"));
                entity.setSort(rs.getInt("sort"));
                entity.setDelFlag(rs.getString("del_flag"));
                entity.setCreateTime(rs.getDate("create_time"));
                entity.setUpdateTime(rs.getDate("update_time"));
                return entity;
            }
        });

        return list;
    }

    @Override
    public List<ModulePortal> queryModulePortalByCondition(Map<String, Object> paramMap) {
        StringBuffer sql = new StringBuffer();

        sql.append("select * from app_tb_module_portal where 1=1 ");
        if(null != paramMap && null != paramMap.get("id")){
            Integer id = Integer.parseInt(String.valueOf(paramMap.get("id")));
            sql.append(" and id = "+id);
        }

        if(null != paramMap && null != paramMap.get("itemName")){
            String itemName = String.valueOf(paramMap.get("itemName"));
            sql.append(" and itemName like '%"+itemName+"%' ");
        }

        if(null != paramMap && null != paramMap.get("mainTitle")){
            String mainTitle = String.valueOf(paramMap.get("mainTitle"));
            sql.append(" and main_title like '%"+mainTitle+"%' ");
        }

        if(null != paramMap && null != paramMap.get("subTitle")){
            String subTitle = String.valueOf(paramMap.get("subTitle"));
            sql.append(" and sub_title like '%"+subTitle+"%' ");
        }

        if(null != paramMap && null != paramMap.get("isVisible")){
            String isVisible = String.valueOf(paramMap.get("isVisible"));
            sql.append(" and is_visible = '"+isVisible+"'");
        }

        if(null != paramMap && null != paramMap.get("sort")){
            Integer sort = Integer.parseInt(String.valueOf(paramMap.get("sort")));
            sql.append(" and sort = "+sort);
        }

        if(null != paramMap && null != paramMap.get("delFlag")){
            String delFlag = String.valueOf(paramMap.get("delFlag"));
            sql.append(" and del_flag = '"+delFlag+"'");
        }

        List<ModulePortal> list = jdbcTemplate.query(sql.toString(), new RowMapper(){
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                ModulePortal entity = new ModulePortal();
                entity.setId(rs.getInt("id"));
                entity.setItemName(rs.getString("item_name"));
                entity.setMainTitle(rs.getString("main_title"));
                entity.setSubTitle(rs.getString("sub_title"));
                entity.setJumpUrl(rs.getString("jump_url"));
                entity.setIsVisible(rs.getString("is_visible"));
                entity.setSort(rs.getInt("sort"));
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
    public boolean updateModulePortalById(ModulePortal entity) {
        if (null == entity || null == entity.getId() || 0 == entity.getId()){
            logger.info("ModulePortalServiceImpl.updateModulePortalById 参数异常 ");
            return false;
        }

        final  String sql = "update app_tb_module_portal set item_name = ? ,icon_url = ?,main_title = ?,sub_title = ?,jump_url = ?,is_visible = ?,sort = ?,update_time = ? where id = ? ";
        return jdbcTemplate.update(sql,new Object[]{entity.getItemName(),entity.getIconUrl(),entity.getMainTitle(),entity.getSubTitle(),entity.getJumpUrl(),entity.getIsVisible(),entity.getSort(),new Date(),entity.getId()}) > 0 ? true:false;
    }

    @Override
    public boolean delModulePortalById(Integer id) {
            if (null == id || id == 0){
                logger.info("ModulePortalServiceImpl.delModulePortalById 参数异常 ");
                return false;
            }

            final String sql = "update app_tb_module_portal set del_flag = ? ,update_time = ? where id = ? ";
            return jdbcTemplate.update(sql,new Object[]{"1",new Date(),id}) > 0 ? true:false;
        }
}
