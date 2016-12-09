package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import com.wondersgroup.healthcloud.services.bbs.dto.AdminVestInfoDto;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * Created by ys on 16/12/06.
 * 管理员设置
 *
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/admin")
public class BbsAdminController {

    private static final Logger logger = LoggerFactory.getLogger("BbsAdminController");
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BbsAdminService bbsAdminService;
    /**
     * 绑定手机段用户
     * @return
     */
    @Admin
    @RequestMapping(value = "/bindAppUser", method = RequestMethod.POST)
    public JsonResponseEntity bindAppUser(@RequestHeader String userId, @RequestBody String request) {
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String mobile = reader.readString("mobile", false);
        String code = reader.readString("code", false);

        Boolean result = userAccountService.validateCode(mobile, code, false);
        if (!result){
            throw new CommonException(2001, "短信验证码验证错误");
        }

        bbsAdminService.bindAppUser(userId, mobile);

        entity.setMsg("绑定成功");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/sendPhoneCode", method = RequestMethod.POST)
    public JsonResponseEntity sendPhoneCode(@RequestHeader String userId, @RequestParam String mobile) {

        JsonResponseEntity entity = new JsonResponseEntity();
        RegisterInfo registerInfo = userService.findRegisterInfoByMobile(mobile);
        if (null == registerInfo){
            throw new CommonException(2002, "手机号没有注册!");
        }
        User bindUser = userRepository.findByBindUid(registerInfo.getRegisterid());
        if (null != bindUser && !bindUser.getUserId().equals(userId)){
            throw new CommonException(2003, "该手机号已被其他管理员绑定!");
        }
        userAccountService.getVerifyCode(mobile, 3);
        entity.setMsg("短信验证码发送成功");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/addAssociationUser", method = RequestMethod.POST)
    public JsonResponseEntity addAssociationUser(@RequestHeader String appUid, @RequestBody AdminVestInfoDto vestInfoDto) {
        JsonResponseEntity entity = new JsonResponseEntity();
        bbsAdminService.addUpdateAdminVestUser(appUid, vestInfoDto);
        entity.setMsg("绑定成功");
        return entity;
    }

    /**
     * 管理员下面的关联小号
     */
    @Admin
    @RequestMapping(value = "/associations", method = RequestMethod.GET)
    public JsonResponseEntity<List<Map<String, Object>>> associations(@RequestHeader String appUid){
        JsonResponseEntity<List<Map<String, Object>>> entity = new JsonResponseEntity();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> info = new HashMap<>();
        info.put("uid", appUid);
        info.put("name", "本人");
        list.add(info);
        List<String> vestUids = bbsAdminService.getAdminVestUidsByAdminUid(appUid);
        if (vestUids != null && !vestUids.isEmpty()){
            Set<String> appUids = new HashSet<>();
            Map<String, RegisterInfo> vestUserMap = userService.findByUids(appUids);
            if (null != vestUserMap){
                for (RegisterInfo registerInfo : vestUserMap.values()) {
                    Map<String, Object> infoTmp = new HashMap<>();
                    infoTmp.put("uid", registerInfo.getRegisterid());
                    infoTmp.put("name", registerInfo.getNickname());
                    list.add(infoTmp);
                }
            }
        }
        entity.setData(list);
        return entity;
    }

}
