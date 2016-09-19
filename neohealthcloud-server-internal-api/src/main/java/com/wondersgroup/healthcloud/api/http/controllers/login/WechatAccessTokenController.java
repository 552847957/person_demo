package com.wondersgroup.healthcloud.api.http.controllers.login;

import com.google.common.collect.ImmutableMap;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhuchunliu on 2016/9/19.
 */
@RestController
@RequestMapping
public class WechatAccessTokenController {


    private static final Logger logger = Logger.getLogger(WechatAccessTokenController.class);

    @Autowired
    private UserAccountService userAccountService;

    /**
     * 微信登录
     * @param token
     * @param openid
     * @return
     */
    @RequestMapping(value = "/token/thirdparty/wechat", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<String> wechatLogin(
            @RequestParam String token,
            @RequestParam String openid) {
        JsonResponseEntity body = new JsonResponseEntity<>();
        AccessToken accessToken =  userAccountService.wechatLogin(token, openid);
        body.setMsg("登录成功");
        body.setData(ImmutableMap.of("token",accessToken.getToken()));
        logger.info("GET url = api/token/thirdparty/wechat,assess_token="+accessToken.getToken()+
                "&token="+token+"&openid="+openid);
        return body;
    }
}
