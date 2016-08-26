package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.services.user.UserService;
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
}
