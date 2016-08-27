package com.wondersgroup.healthcloud.services.permission.impl;

import com.wondersgroup.healthcloud.jpa.entity.permission.Menu;
import com.wondersgroup.healthcloud.services.permission.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/26.
 */
@Service("permissionServiceImpl")
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jt;

    @Override
    public List<Map<String,Object>> getRoleEnnameByUser(String userId,Boolean admin) {
        String sql = "select enname from tb_neopermission_role where role_id in " +
                " (select role_id from tb_neopermission_user_role where user_id = '"+userId+"')";
        if(admin){
           sql = "select enname from tb_neopermission_role";
        }
        return getJt().queryForList(sql);
    }

    @Override
    public List<Map<String,Object>> getMenuPermissionByUser(String userId,Boolean admin) {
        String sql = "select permission from tb_neopermission_menu where " +
                " menu_id in("+this.getUserMenu(userId)+") and " +
                " is_show = '1' and del_flag = '0'";
        if(admin){
            sql = "select permission from tb_neopermission_menu where is_show = '1' and del_flag = '0'";
        }
        return getJt().queryForList(sql);
    }


    @Override
    public List<Menu> getMenuByParentId(String userId, String parentId,Boolean admin) {
        String sql = null;
        if(admin){
            sql = "select * from tb_neopermission_menu m where " +
                    " parent_id = '"+parentId+"' and is_show = '1' and del_flag = '0'  order by sort asc";
        }else{
            sql = "select * from tb_neopermission_menu m where " +
                    " menu_id in ("+this.getUserMenu(userId)+") and " +
                    " parent_id = '"+parentId+"' and is_show = '1' and del_flag = '0'  order by sort asc";
        }
        return getJt().query(sql, new RowMapper<Menu>() {
            @Override
            public Menu mapRow(ResultSet resultSet, int i) throws SQLException {
                Menu menu = new Menu();
                menu.setMenuId(resultSet.getString("menu_id"));
                menu.setName(resultSet.getString("name"));
                menu.setHref(resultSet.getString("href"));
                menu.setIcon(resultSet.getString("icon"));
                return menu;
            }
        });
    }

    private String getUserMenu(String userId){
        return  " select role_menu.menu_id from tb_neopermission_user us " +
                " join tb_neopermission_user_role us_role on us.user_id = us_role.user_id " +
                " join tb_neopermission_role role on us_role.role_id = role.role_id" +
                " join tb_neopermission_role_menu role_menu on role.role_id = role_menu.role_id" +
                " where us.user_id = '"+userId+"' and role.useable = '1'";
    }
    /**
     * 获取jdbc template
     *
     * @return
     */
    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }
}
