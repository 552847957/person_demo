package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.UserInfoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by longshasha on 16/9/1.
 */
@RestController
@RequestMapping(value = "/api/user")
public class userController {

    @Autowired
    private UserService userService;


    @PostMapping(path = "/userInfo/update")
    public JsonResponseEntity updateUserInfo(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        JsonResponseEntity body = new JsonResponseEntity<>();
        UserInfoForm form = new UserInfoForm();

        form.registerId = reader.readString("uid", false);

        form.height = reader.readObject("height", true, Integer.class);
        form.weight = reader.readObject("weight", true, Float.class);
        form.waist = reader.readObject("waist", true, Float.class);
        try {
            userService.updateUserInfo(form);
        }catch (Exception e){
            body.setData(3101);
            body.setMsg("信息修改失败");
            return body;
        }

        body.setMsg("信息修改成功");
        return body;
    }
}
