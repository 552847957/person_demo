package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.api.http.dto.user.UserAccountAndSessionDTO;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by longshasha on 16/8/4.
 */

@RestController
@RequestMapping("/api")
public class UserAccessTokenController {
    private static final Logger logger = Logger.getLogger(UserAccessTokenController.class);

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserController userController;

    /**
     * 账号 密码登录
     * @param account
     * @param password
     * @return
     */
    @WithoutToken
    @GetMapping(path = "/token")
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> fetchToken(@RequestParam String account,
                                                                   @RequestParam String password
    ) {

        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.login(account,password)));
        body.setMsg("登录成功");
        attachInfo(body);
        logger.info("");
        return body;
    }

    /**
     * 游客登录
     * @return
     */
    @WithoutToken
    @VersionRange
    @GetMapping(path = "/token/guest")
    public JsonResponseEntity<UserAccountAndSessionDTO> fetchGuestToken() {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.guestLogin()));
        return body;
    }

    /**
     * 使用验证码动态登录
     * @param mobile
     * @param verify_code
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/token/fast", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> fastFetchToken(@RequestParam String mobile,
                                                                       @RequestParam String verify_code) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.fastLogin(mobile, verify_code,false)));//改为false
        body.setMsg("登录成功");
        attachInfo(body);
        return body;
    }

    /**
     * 微信登录
     * @param token
     * @param openid
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/token/thirdparty/wechat", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> wechatLogin(@RequestParam String token,
                                                                    @RequestParam String openid) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.wechatLogin(token, openid)));
        body.setMsg("登录成功");
        attachInfo(body);
        return body;
    }

    /**
     * 微博登录
     * @param token 微博的token
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/token/thirdparty/weibo", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> weiboLogin(@RequestParam String token) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.weiboLogin(token)));
        body.setMsg("登录成功");
        attachInfo(body);
        return body;
    }

    /**
     * QQ 登录
     * @param token QQ的token
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/token/thirdparty/qq", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> qqLogin(@RequestParam String token) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.qqLogin(token)));
        body.setMsg("登录成功");
        attachInfo(body);
        return body;
    }


    @RequestMapping(value = "/token/logout", method = RequestMethod.DELETE)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> deleteToken(@RequestHeader("access-token") String token) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.logout(token)));
        body.setMsg("退出成功");
        return body;
    }


    private void attachInfo(JsonResponseEntity<UserAccountAndSessionDTO> body) {
        body.getData().setInfo(userController.getInfo(body.getData().getUid()));
    }

}
