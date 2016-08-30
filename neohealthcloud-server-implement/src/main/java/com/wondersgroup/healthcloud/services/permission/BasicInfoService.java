package com.wondersgroup.healthcloud.services.permission;

import com.wondersgroup.healthcloud.jpa.entity.permission.Role;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/13.
 */
public interface BasicInfoService {


    int findAllUserTotal(Map<String, Object> parameters, String admin);

    List<Map<String,Object>> findAllUser(Map<String, Object> parameters, String admin, int nowPage, int pageSize);

    /**
     * 根据角色主键获取用户列表信息
     * @param roleId
     * @return
     */
    List<Map<String,Object>> getRoleUserInfo(String roleId);


    void updateRoleInfo(Role role, String menuIds);

    /**
     * 获取用户权限选择信息
     * @param userId
     * @return
     */
    List<Map<String,Object>> getUserRoleInfo(String userId);

    void updateUserInfo(User user, String roleIds);

    List<Map<String,Object>> findAllRole(String userId);



}
