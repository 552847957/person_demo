package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/25.
 */

@RestController
@RequestMapping(value = "/admin/user")
public class UserManageController {

    @Autowired
    private UserService userService;

    @Autowired
    private DictCache dictCache;


    /**
     * 用户列表
     */
    @Admin
    @PostMapping(value = "/list")
    public Pager userList(@RequestBody Pager pager){
        int pageNum = 1;
        if(pager.getNumber()!=0)
            pageNum = pager.getNumber();

        List<Map<String,Object>> mapList = userService.findUserListByPager(pageNum,pager.getSize(),pager.getParameter());

        int totalSize = userService.countUserByParameter(pager.getParameter());
        pager.setTotalElements(totalSize);
        pager.setData(mapList);
        return pager;
    }

    /**
     * 用户详情
     * @param registerid
     * @return
     */
    @GetMapping(value = "/detail")
    public JsonResponseEntity<Map<String,Object>> getUserDetail(@RequestParam(required = true) String registerid){
        JsonResponseEntity<Map<String,Object>> body = new JsonResponseEntity<>();
        Map<String,Object> user = userService.findUserDetailByUid(registerid);
        if(user==null){
            body.setCode(1000);
            body.setMsg("未找到对应用户");
            return body;
        }
        String addressDisplay = "";
        Address address = userService.getAddress(registerid);
        if (address != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(StringUtils.trimToEmpty(dictCache.queryArea(address.getProvince())));
            buffer.append(StringUtils.trimToEmpty(dictCache.queryArea(address.getCity())));
            buffer.append(StringUtils.trimToEmpty(dictCache.queryArea(address.getCounty())));
            buffer.append(StringUtils.trimToEmpty(dictCache.queryArea(address.getTown())));
            buffer.append(StringUtils.trimToEmpty(dictCache.queryArea(address.getCommittee())));
            buffer.append(StringUtils.trimToEmpty(address.getOther()));
            addressDisplay = StringUtils.trimToNull(buffer.toString());

        }
        user.put("address",addressDisplay);
        body.setData(user);
        return body;
    }
}
