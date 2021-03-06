package com.wondersgroup.healthcloud.services.permission.impl;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.permission.*;
import com.wondersgroup.healthcloud.jpa.repository.administrative.AreaRepository;
import com.wondersgroup.healthcloud.jpa.repository.administrative.CityRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.RoleMenuRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.RoleRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRoleRepository;
import com.wondersgroup.healthcloud.services.permission.BasicInfoService;
import com.wondersgroup.healthcloud.services.permission.dto.MenuDTO;
import com.wondersgroup.healthcloud.utils.MenuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.*;

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
    @Autowired
    private AreaRepository areaRepo;
    @Autowired
    private CityRepository cityRepo;

    private JdbcTemplate jt;


    @Override
    public int findAllUserTotal(Map<String, Object> param, String admin) {
        String sql = "select count(1) as total from tb_neopermission_user where loginname <> '" + admin + "'";

        if (param != null && param.size() > 0) {
            if (!StringUtils.isEmpty(param.get("delFlag"))) {
                sql += " and del_flag = '" + param.get("delFlag") + "'";
            }
            if (!StringUtils.isEmpty(param.get("username"))) {
                sql += " and (username like '%" + param.get("username") + "%' or loginname like '%" + param.get("username") + "%')";
            }
            if (!StringUtils.isEmpty(param.get("mainArea"))) {
                sql += " and main_area = '" + param.get("mainArea") + "'";
            }
            if (!StringUtils.isEmpty(param.get("specArea"))) {
                sql += " and spec_area = '" + param.get("specArea") + "'";
            }
        }

        return Integer.parseInt(getJt().queryForList(sql).get(0).get("total").toString());
    }

    @Override
    public List<User> findAllUser(Map<String, Object> param, String admin, int nowPage, int pageSize) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT user_id, loginname, username, main_area, spec_area, del_flag, create_date FROM tb_neopermission_user")
                .append(" WHERE loginname <> '" + admin + "'");

        if (param != null && param.size() > 0) {
            if (!StringUtils.isEmpty(param.get("delFlag"))) {
                sql.append(" AND del_flag = '" + param.get("delFlag") + "'");
            }
            if (!StringUtils.isEmpty(param.get("username"))) {
                sql.append(" AND (username LIKE '%" + param.get("username") + "%' OR loginname LIKE '%" + param.get("username") + "%')");
            }
            if (!StringUtils.isEmpty(param.get("mainArea"))) {
                sql.append(" AND main_area = '" + param.get("mainArea") + "'");
            }
            if (!StringUtils.isEmpty(param.get("specArea"))) {
                sql.append(" AND spec_area = '" + param.get("specArea") + "'");
            }
        }
        sql.append(" LIMIT " + (nowPage - 1) * pageSize + "," + pageSize);
        List<User> userList = getJt().query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper(User.class));
        if (userList != null && userList.size() > 0) {
            StringBuffer tmpSql = null;
            for (User user : userList) {
                if (!StringUtils.isEmpty(user.getUserId())) {
                    tmpSql = new StringBuffer();
                    tmpSql.append("SELECT ur.role_id, CASE WHEN r.name IS NULL THEN '未知' ELSE r.name END name")
                            .append(" FROM tb_neopermission_user_role ur LEFT JOIN tb_neopermission_role r ON ur.role_id = r.role_id")
                            .append(" WHERE user_id = '" + user.getUserId() + "'");
                    user.setRoleList(getJt().query(tmpSql.toString(), new Object[]{}, new BeanPropertyRowMapper(RoleEntity.class)));
                    if (!StringUtils.isEmpty(user.getMainArea())) {
                        user.setMainAreaName(cityRepo.findNameByCityId(user.getMainArea()));
                    }
                    if (!StringUtils.isEmpty(user.getSpecArea())) {
                        user.setSpecAreaName(areaRepo.findNameByAreaId(user.getSpecArea()));
                    }
                }
            }
        }
        return userList;
    }

    /**
     * 根据角色主键获取用户列表信息
     *
     * @param roleId
     * @return
     */
    @Override
    public List<User> getRoleUserInfo(String roleId) {
        String sql = "SELECT user.user_id AS userId,user.loginname,user.username" +
                " FROM tb_neopermission_user user  " +
                " WHERE user_id IN (SELECT user_id FROM tb_neopermission_user_role WHERE role_id = '" + roleId + "' AND del_flag = '0')" +
                " AND user.del_flag = '0'";

        return getJt().query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper<User>(User.class));
    }


    @Transactional
    @Override
    public void updateRoleInfo(Role role, String menuIds) {
        if (StringUtils.isEmpty(role.getRoleId())) {
            role.setRoleId(IdGen.uuid());
            role.setCreateDate(new Date());
        }
        role.setUpdateDate(new Date());
        HashSet<String> menuSet = new HashSet<>();
        for (String menuId : menuIds.split(",")) {
            menuSet.add(menuId);
        }
        roleMenuRepo.deleteRoleMenu(role.getRoleId());
        roleRepo.save(role);
        if (menuSet.size() > 0) {
            Iterator<String> iterator = menuSet.iterator();
            while (iterator.hasNext()) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setId(IdGen.uuid());
                roleMenu.setRoleId(role.getRoleId());
                roleMenu.setMenuId(iterator.next());
                roleMenu.setDelFlag("0");
                roleMenuRepo.save(roleMenu);
            }
        }
    }


    /**
     * 获取用户权限选择信息
     *
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
        for (String roleId : roleIds.split(",")) {
            if (StringUtils.isEmpty(roleId)) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setId(IdGen.uuid());
            userRole.setRoleId(roleId);
            userRole.setUserId(user.getUserId());
            userRole.setCreateDate(new Date());
            userRole.setDelFlag("0");
            userRoleRepo.save(userRole);
        }

    }

    @Override
    public List<Map<String, Object>> findAllRole(String userId) {
        String sql = "select role.role_id,name,user_role.role_id as check_role_id from tb_neopermission_role role left join \n" +
                "(select role_id from tb_neopermission_user_role where user_id = '" + userId + "') user_role\n" +
                "on role.role_id = user_role.role_id\n" +
                " where del_flag = '0' order by role.name asc";
        return getJt().queryForList(sql);
    }

    @Override
    public List<Role> findUserRoles(String userId) {
        String sql = "select role_id,name," +
                " case when (select count(1) from tb_neopermission_user_role where del_flag = '0' and  role_id = role.role_id)<>0 then 1 else 0 END checked " +
                " from tb_neopermission_role role where del_flag = '0'";
        //return getJt().queryForList(sql);
        return null;
    }

    @Override
    public MenuDTO findUserMunuPermission(User user) {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT                                              ");
        sql.append("   c.*                                               ");
        sql.append(" FROM                                                ");
        sql.append("   tb_neopermission_user_role a,                     ");
        sql.append("   tb_neopermission_role_menu b,                     ");
        sql.append("   tb_neopermission_menu c                           ");
        sql.append(" WHERE a.del_flag = '0'                              ");
        sql.append("   AND b.del_flag = '0'                              ");
        sql.append("   AND c.del_flag = '0'                              ");
        sql.append("   AND c.type = '1'                                  ");
        sql.append("   AND a.user_id = '" + user.getUserId() + "'        ");
        sql.append("   AND a.role_id = b.role_id                         ");
        sql.append("   AND b.menu_id = c.menu_id                         ");
        sql.append("  order by c.level asc,c.sort asc,c.update_date desc ");
        List<Menu> menuList = getJt().query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper<Menu>(Menu.class));
        List<MenuDTO> list = Lists.newArrayList();
        for (Menu menu : menuList) {
            list.add(new MenuDTO(menu));
        }
        MenuUtils.addMenuChildrenToParent(list);
        MenuDTO menuTree = null;
        for (int i = 0; i < list.size(); i++) {
            // 根节点ID为1
            if (list.get(i) != null && "1".equals(list.get(i).getMenuId())) {
                menuTree = list.get(i);
                break;
            }
        }
        this.dealBbsMenu(user, menuTree);
        return menuTree;
    }

    /**
     * 为圈子标签做特殊处理
     * 如果管理员没有绑定手机段用户, 一级menu跳转到绑定页面
     */
    private void dealBbsMenu(User user, MenuDTO menuTree){
        if (null == menuTree || null == menuTree.getChildren()){
            return;
        }
        for (MenuDTO menuDTO : menuTree.getChildren()){
            if (!StringUtils.isEmpty(menuDTO.getHref())
                    && menuDTO.getHref().equalsIgnoreCase("app.coterieManage")){
                if (StringUtils.isEmpty(user.getBindUid())){
                    menuDTO.setChildren(null);
                }else {
                    menuDTO.setHref("");
                }
            }
        }
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
