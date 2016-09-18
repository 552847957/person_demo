package com.wondersgroup.healthcloud.api.http.controllers.family;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by longshasha on 16/9/18.
 */
@RestController
@RequestMapping("/api/child")
public class ChildController {

    @Autowired
    private UserAccountService userAccountService;

    /**
     * 提交实名认证信息
     *
     * @return
     */
    @VersionRange
    @PostMapping(path = "/verification/submit")
    public JsonResponseEntity<String> verificationSubmit(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid", false);//监护人Id
        String name = reader.readString("name", false);//儿童的真实姓名
        String idCard = reader.readString("idcard", false);//儿童的身份证号
        String idCardFile = reader.readString("idCardFile", false);//户口本(儿童身份信息页照片)
        String birthCertFile = reader.readString("birthCertFile", false);//出生证明(照片)
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        name = name.trim();//去除空字符串
        idCard = idCard.trim();
        userAccountService.childVerificationSubmit(id, name, idCard, idCardFile, birthCertFile);
        body.setMsg("提交成功");
        return body;
    }


}
