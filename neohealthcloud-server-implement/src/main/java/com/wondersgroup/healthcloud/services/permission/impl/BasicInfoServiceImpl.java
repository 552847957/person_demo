package com.wondersgroup.healthcloud.services.permission.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.permission.Role;
import com.wondersgroup.healthcloud.jpa.entity.permission.RoleMenu;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.permission.UserRole;
import com.wondersgroup.healthcloud.jpa.repository.permission.RoleMenuRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.RoleRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRoleRepository;
import com.wondersgroup.healthcloud.services.permission.BasicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/13.
 */
@Service("basicInfoServiceImpl")
public class BasicInfoServiceImpl implements BasicInfoService {
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private RoleMenuRepository roleMenuRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserRoleRepository userRoleRepo;
    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jt;



    @Override
    public int findAllUserTotal(Map<String, Object> parameters,String admin) {
        String sql ="select count(1) as total from tb_neopermission_user where del_flag = '0' and loginname <> '"+admin+"'";
        Iterator iterator = parameters.keySet().iterator();

        while (iterator.hasNext()){
            String key = iterator.next().toString();
            if(StringUtils.isEmpty(parameters.get(key))){
                continue;
            }
            sql +=" and (username like '%"+parameters.get(key)+"%' or loginname like '%"+parameters.get(key)+"%')";
        }
        return Integer.parseInt(getJt().queryForList(sql).get(0).get("total").toString());
    }

    @Override
    public List<Map<String,Object>> findAllUser(Map<String, Object> parameters,String admin, int nowPage, int pageSize) {
        String sql ="select user_id as userId,loginname,username," +
                " case locked when  '0' then '启用' else '禁用' end as lockedName from tb_neopermission_user where del_flag = '0' and loginname <> '"+admin+"'";
        Iterator iterator = parameters.keySet().iterator();

        while (iterator.hasNext()){
            String key = iterator.next().toString();
            if(StringUtils.isEmpty(parameters.get(key))){
                continue;
            }

            if(key.equals("username")){
                sql +=" and (username like '%"+parameters.get(key)+"%' or loginname like '%"+parameters.get(key)+"%')";
            }
        }
        sql +=" limit "+(nowPage-1)*pageSize+","+pageSize;

        return getJt().queryForList(sql);
    }

    /**
     * 根据角色主键获取用户列表信息
     * @param roleId
     * @return
     */
    @Override
    public List<Map<String, Object>> getRoleUserInfo(String roleId) {
        String sql = "select user.user_id as userId,user.loginname,user.username"+
                " from tb_neopermission_user user  " +
                " where user_id in (select user_id from tb_neopermission_user_role where role_id = '"+roleId+"' and del_flag = '0')" +
                " and user.del_flag = '0'";

        return getJt().queryForList(sql);
    }


    @Transactional
    @Override
    public void updateRoleInfo(Role role, String menuIds) {
        if (StringUtils.isEmpty(role.getRoleId())) {
            role.setRoleId(IdGen.uuid());
            role.setCreateDate(new Date());
        }
        roleRepo.save(role);
        roleMenuRepo.deleteRoleMenu(role.getRoleId());
        if(!StringUtils.isEmpty(menuIds)) {
            for (String menuId : menuIds.split(",")){
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setId(IdGen.uuid());
                roleMenu.setRoleId(role.getRoleId());
                roleMenu.setMenuId(menuId);
                roleMenuRepo.save(roleMenu);
            }
        }
    }


    /**
     * 获取用户权限选择信息
     * @param userId
     * @return
     */
    @Override
    public List<Map<String, Object>> getUserRoleInfo(String userId) {
        String sql = "select role_id,name," +
                " case when (select count(1) from tb_neopermission_user_role where del_flag = '0' and  role_id = role.role_id)<>0 then 1 else 0 END checked " +
                " from tb_neopermission_role role where del_flag = '0'";
        return getJt().queryForList(sql);
    }

    @Override
    public void updateUserInfo(User user, String roleIds) {
        userRepo.save(user);
        userRoleRepo.deleteByUserId(user.getUserId());
        for(String roleId:roleIds.split(",")){
            if(StringUtils.isEmpty(roleId)){
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setId(IdGen.uuid());
            userRole.setRoleId(roleId);
            userRole.setUserId(user.getUserId());
            userRole.setCreateDate(new Date());
            userRoleRepo.save(userRole);
        }

    }

    @Override
    public List<Map<String,Object>> findAllRole(String userId) {
        String sql = "select role.role_id,name,user_role.role_id as check_role_id from tb_neopermission_role role left join \n" +
                "(select role_id from tb_neopermission_user_role where user_id = '"+userId+"') user_role\n" +
                "on role.role_id = user_role.role_id\n" +
                " where role.useable = '1' and del_flag = '0' order by role.name asc";
        return getJt().queryForList(sql);
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
