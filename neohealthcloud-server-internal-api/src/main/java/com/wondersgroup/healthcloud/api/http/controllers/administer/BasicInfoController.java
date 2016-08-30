package com.wondersgroup.healthcloud.api.http.controllers.administer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import com.wondersgroup.healthcloud.api.helper.PropertiesUtil;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.permission.Role;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.permission.UserRole;
import com.wondersgroup.healthcloud.services.permission.BasicInfoService;
import com.wondersgroup.healthcloud.services.permission.MenuService;
import com.wondersgroup.healthcloud.jpa.repository.permission.RoleRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRoleRepository;
import com.wondersgroup.healthcloud.api.shiro.PasswordHelper;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2015/11/12.
 * <p/>
 * 基础资料的维护：人员，角色，分组，人员角色，医院分组信息
 */
@Controller
@RequestMapping(value = "/api/basicInfo")
public class BasicInfoController {
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserRoleRepository userRoleRepo;
    @Autowired
    private BasicInfoService basicInfoService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private PropertiesUtil propertiesUtil;

    /**
     * 添加用户信息
     *
     * @param userId
     * @return
     */
    @RequiresPermissions(value = {"user:add", "user:edit"}, logical = Logical.OR)
    @RequestMapping(value = "/userAdd", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView userAdd(@RequestParam(value = "userId", required = false) String userId) {
        Map<String, Object> map = Maps.newHashMap();
        User user = new User();
        if (!StringUtils.isEmpty(userId)) {
            user = userRepo.findOne(userId);
        }
//        设置角色选中状态
        List<Map<String, Object>> list = basicInfoService.findAllRole(userId);
        List<User.Role> roleList = Lists.newArrayList();
        for (Map<String, Object> role : list) {
            User.Role child = new User().new Role();
            child.setRoleId(role.get("role_id").toString());
            child.setName(role.get("name").toString());
            child.setChecked((null == role.get("check_role_id")) ? false : true);
            roleList.add(child);
        }

        user.setRoleList(roleList);

        map.put("info", user);
        ModelAndView mav = new ModelAndView("authManager/userAdd", map);
        return mav;
    }

    /**
     * 添加角色信息
     *
     * @param roleId 角色主键
     * @return
     */
    @RequiresPermissions(value = {"role:add", "role:edit"}, logical = Logical.OR)
    @RequestMapping(value = "/roleAdd", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView roleAdd(@RequestParam(value = "roleId", required = false) String roleId) {
        Map<String, Object> map = Maps.newHashMap();
        Role role = new Role();
        if (!StringUtils.isEmpty(roleId)) {
            role = roleRepo.findOne(roleId);
        }
        List<Map<String, Object>> list = menuService.getMenuByRole(roleId);//获取选中的菜单信息
        List<Role.Menu> menuList = Lists.newArrayList();
        for (Map<String, Object> child : list) {
            Role.Menu menu = new Role().new Menu();
            menu.setId(child.get("menu_id").toString());
            menu.setName(StringUtils.isEmpty(child.get("name")) ? null : child.get("name").toString());
            menu.setPId(StringUtils.isEmpty(child.get("parent_id")) ? null : child.get("parent_id").toString());
            if (StringUtils.isEmpty(child.get("checked")) || child.get("checked").toString().equals("0")) {
                menu.setChecked(false);
            } else {
                menu.setChecked(true);
            }
            menuList.add(menu);
        }
        role.setMenuTree(new Gson().toJson(menuList));
        map.put("info", role);
        ModelAndView mav = new ModelAndView("authManager/roleAdd", map);
        return mav;
    }


    /**
     * 获取用户列表
     * 传参数 hospitalId 医院id，loginname 登录名，username用户名
     *
     * @return
     */
    @RequiresPermissions(value = {"user:view", "user:add", "user:edit", "user:delete"}, logical = Logical.OR)
    @PostMapping(value = "/user/list")
    @ResponseBody
    public JsonResponseEntity<Pager> getUserInfoList(@RequestBody Pager pager) {

        JsonResponseEntity<Pager> response = new JsonResponseEntity<Pager>();
        if (null != pager) {
            List<Map<String, Object>> list = basicInfoService.findAllUser(pager.getParameter(),
                    propertiesUtil.getAccount(), pager.getNumber(), pager.getSize());

            int total = basicInfoService.findAllUserTotal(pager.getParameter(), propertiesUtil.getAccount());
            pager.setTotalPages((total + pager.getSize() - 1) / pager.getSize());//页数
            pager.setTotalElements(total);            //记录数
            pager.setData(list);        //结果集
        }
        response.setData(pager);
        return response;
    }

    /**
     * 更新用户信息
     * Updated upstream
     *
     * @return
     */
    @RequiresPermissions(value = {"user:add", "user:edit"}, logical = Logical.OR)
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity updateUser(User user, @RequestParam(value = "roleIds") String roleIds) {
        JsonResponseEntity response = new JsonResponseEntity();
        try {
            User original = userRepo.findOne(user.getUserId());
            if (StringUtils.isEmpty(user.getUserId())) {
                user.setUserId(IdGen.uuid());
                user.setCreateDate(new Date());
                user.setLocked("0");
            }
            if (!StringUtils.isEmpty(user.getPassword())) {
                user.setPassword(PasswordHelper.encryptPassword(user.getPassword()));
            } else {
                user.setPassword(original.getPassword());
            }
            user.setLocked(null == original ? "0" : original.getLocked());
            basicInfoService.updateUserInfo(user, roleIds);
            response.setMsg("保存成功");

        } catch (Exception e) {
            e.printStackTrace();
            response.setMsg("保存失败");
        }
        return response;
    }

    /**
     * 更新用户信息【基本信息】
     * Updated upstream
     *
     * @return
     */
    @RequestMapping(value = "/user/update/info", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity updateUserInfo(
            @RequestParam(value = "userId", required = true) String userId,
            @RequestParam(value = "username", required = true) String username) {
        JsonResponseEntity response = new JsonResponseEntity();
        try {
            User user = userRepo.findOne(userId);
            user.setUsername(username);
            userRepo.save(user);
            response.setMsg("保存成功");

        } catch (Exception e) {
            e.printStackTrace();
            response.setMsg("保存失败");
        }
        return response;
    }

    /**
     * 更新用户信息
     * Updated upstream
     *
     * @return
     */
    @RequestMapping(value = "/user/update/password", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity updateUserPassword(
            @RequestParam(value = "userId", required = true) String userId,
            @RequestParam(value = "oldPassword", required = true) String oldPassword,
            @RequestParam(value = "newPassword", required = true) String newPassword) {
        JsonResponseEntity response = new JsonResponseEntity();
        try {
            User user = userRepo.findOne(userId);
            if (!PasswordHelper.encryptPassword(oldPassword).equals(user.getPassword())) {
                response.setCode(1000);
                response.setData("旧密码输入错误");
            } else {
                user.setPassword(PasswordHelper.encryptPassword(newPassword));
                userRepo.save(user);
                response.setMsg("保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMsg("保存失败");
        }
        return response;
    }

    /**
     * 锁定用户信息
     * Updated upstream
     *
     * @return
     */
    @RequiresPermissions("user:edit")
    @RequestMapping(value = "/user/locked", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity lockedUser(
            @RequestParam(value = "userid", required = true) String userid) {
        JsonResponseEntity response = new JsonResponseEntity();
        try {
            User user = userRepo.findOne(userid);
            user.setUpdateDate(new Date());
            user.setLocked(user.getLocked() == null ? "1" : String.valueOf(1 - Integer.parseInt(user.getLocked())));
            userRepo.save(user);
            response.setMsg("操作成功");

        } catch (Exception e) {
            e.printStackTrace();
            response.setMsg("操作失败");
        }
        return response;
    }

    /**
     * 删除用户信息
     *
     * @param userIds 用户主键集合，多个用户id，用,间隔
     * @return
     */
    @RequiresPermissions("user:delete")
    @RequestMapping(value = "/user/delete", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity deleteUerInfo(@RequestParam(value = "userIds", required = true) String userIds) {
        userRepo.deteleUserInfo(userIds.split(","), new Date());

        JsonResponseEntity response = new JsonResponseEntity();
        return response;
    }

    /**
     * 判断登录名是否已经存在
     *
     * @return 1：表示已经存在，0：表示不存在
     */
    @RequestMapping(value = "/user/nameExist", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponseEntity getNameExist(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "loginname", required = true) String loginname) {

        User user = null;
        if (StringUtils.isEmpty(userId)) {
            user = userRepo.findByLoginName(loginname);
        } else {
            user = userRepo.findByLoginName(userId, loginname);
        }
        JsonResponseEntity response = new JsonResponseEntity();
        if (null == user) {
            response.setCode(0);
        } else {
            response.setCode(1000);
        }
        return response;
    }


    /**
     * 获取角色列表
     *
     * @return
     */
    @RequiresPermissions(value = {"role:view", "role:add", "role:edit", "role:delete"}, logical = Logical.OR)
    @RequestMapping(value = "/role/list", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity<Pager> getRoleInfoList(@RequestParam(required = false) String dtGridPager) {

        JsonResponseEntity<Pager> response = new JsonResponseEntity<Pager>();
        Pager pager = new Gson().fromJson(dtGridPager, Pager.class);
        String name = null == pager.getParameter().get("name") ? "" : pager.getParameter().get("name").toString().trim();
        List<Role> list = roleRepo.findAllRole(name, new PageRequest(pager.getNumber() - 1, pager.getSize()));
        int total = roleRepo.findAllRoleTotal(name);

        pager.setData(list);        //结果集
        pager.setTotalPages((total + pager.getSize() - 1) / pager.getSize());//页数
        pager.setTotalElements(total);
        response.setData(pager);
        return response;
    }

    /**
     * 更新角色信息
     *
     * @param role    角色信息
     * @param menuIds 选中的菜单信息
     * @return
     */
    @RequiresPermissions(value = {"role:add", "role:edit"}, logical = Logical.OR)
    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity updateRoleInfo(Role role, @RequestParam(value = "menuIds") String menuIds) {
        JsonResponseEntity response = new JsonResponseEntity();
        try {
            basicInfoService.updateRoleInfo(role, menuIds);
            response.setMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            response.setMsg("保存失败");
        }
        return response;
    }

    /**
     * 删除角色信息
     *
     * @param roleIds 用户主键集合，多个用户id，用,间隔
     * @return
     */
    @RequiresPermissions("role:delete")
    @RequestMapping(value = "/role/delete", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity deleteRoleInfo(@RequestParam(value = "roleIds", required = true) String roleIds) {
        roleRepo.deteleRoleInfo(roleIds.split(","));
        userRoleRepo.deteleUserRoleInfo(roleIds.split(","));
        JsonResponseEntity response = new JsonResponseEntity();
        return response;
    }

    /**
     * 获取指定角色下用户列表信息
     *
     * @return userId ,loginname, username, hospitalName
     */
    @RequestMapping(value = "/role/user/list", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponseEntity<List<Map<String, Object>>> getRoleUserInfoList(@RequestParam(value = "roleId", required = true) String roleId) {
        JsonResponseEntity<List<Map<String, Object>>> response = new JsonResponseEntity<List<Map<String, Object>>>();
        List<Map<String, Object>> list = basicInfoService.getRoleUserInfo(roleId);
        response.setData(list);
        return response;
    }

    /**
     * 保存用户角色
     *
     * @return
     */
    @RequiresPermissions(value = {"role:add", "role:edit"}, logical = Logical.OR)
    @RequestMapping(value = "/role/user/update", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity<List<Map<String, Object>>> updateRoleUserInfo(UserRole userRole) {
        JsonResponseEntity response = new JsonResponseEntity();
        try {
            if (StringUtils.isEmpty(userRole.getId())) {
                userRole.setId(IdGen.uuid());
                userRole.setCreateDate(new Date());
            }
            userRoleRepo.save(userRole);
            response.setMsg("保存成功");

        } catch (Exception e) {
            e.printStackTrace();
            response.setMsg("保存失败");
        }
        return response;
    }

    /**
     * 删除用户角色
     *
     * @param roleId
     * @param userId
     * @return
     */
    @RequiresPermissions("role:delete")
    @RequestMapping(value = "/role/user/delete", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseEntity<List<Map<String, Object>>> deleteRoleUserInfo(
            @RequestParam(value = "roleId", required = true) String roleId,
            @RequestParam(value = "userId", required = true) String userId) {

        userRoleRepo.deteleUserRoleInfo(roleId, userId);
        JsonResponseEntity response = new JsonResponseEntity();
        return response;
    }

}
