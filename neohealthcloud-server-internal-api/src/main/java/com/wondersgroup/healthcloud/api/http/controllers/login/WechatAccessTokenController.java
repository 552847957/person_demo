package com.wondersgroup.healthcloud.api.http.controllers.login;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.game.WechatRegister;
import com.wondersgroup.healthcloud.jpa.repository.game.WechatRegisterRepository;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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

    @Autowired
    private WechatRegisterRepository wechatRegisterRepo;

    /**
     * 微信登录
     * @param token
     * @param openid
     * @return
     */
    @RequestMapping(value = "/token/thirdparty/wechat", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity wechatLogin(
            @RequestParam String token,
            @RequestParam String openid) {

        logger.info("GET url = api/token/thirdparty/wechat: token="+token+"   openid="+openid);
        if(null == wechatRegisterRepo.getByOpenId(openid)){//用户未创建，则创建用户
            AccessToken accessToken =  userAccountService.wechatLogin(token, openid);
            if(null == accessToken || StringUtils.isEmpty(accessToken.getUid())){
                return new JsonResponseEntity(1001,"万达云登录失败");
            }
            WechatRegister wechat = new WechatRegister();
            wechat.setOpenid(openid);
            wechat.setRegisterid(accessToken.getUid());
            wechat.setCreateDate(DateTime.now().toDate());
            wechat.setDelFlag("0");
            wechatRegisterRepo.save(wechat);
        }
        return new JsonResponseEntity(0,"万达云登录成功");
    }
}
