package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.api.http.dto.user.UserAccountAndSessionDTO;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.services.friend.FriendRelationshipService;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private FriendRelationshipService friendRelationshipService;

    /**
     * 账号 密码登录
     *
     * @param account
     * @param password
     * @return
     */
    @WithoutToken
    @GetMapping(path = "/token")
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> fetchToken(
            @RequestHeader(required = false, name = "request-id") String requestId,
            @RequestParam String account,
            @RequestParam String password) {

        if (!(StringUtils.isNumeric(account) && account.length() == 11 && StringUtils.startsWith(account, "1"))) {
            throw new CommonException(1000, "手机号码格式不正确");
        }

        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.login(account, password)));

        friendRelationshipService.login(account, body.getData().getUid());
        body.setMsg("登录成功");
        attachInfo(body);
        logger.info("GET url = api/token ,requestId=" + requestId + "&uid=" + body.getData().getUid());
        return body;
    }

    /**
     * 游客登录
     *
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
     *
     * @param mobile
     * @param verify_code
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/token/fast", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> fastFetchToken(@RequestHeader(required = false, name = "request-id") String requestId,
                                                                       @RequestParam String mobile,
                                                                       @RequestParam String verify_code) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.fastLogin(mobile, verify_code, false)));//改为false
        body.setMsg("登录成功");
        friendRelationshipService.login(mobile, body.getData().getUid());
        attachInfo(body);
        logger.info("GET, url = api/token/fast,requestId=" + requestId + "&uid=" + body.getData().getUid());
        return body;
    }

    /**
     * 微信登录
     *
     * @param token
     * @param openid
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/token/thirdparty/wechat", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> wechatLogin(
            @RequestHeader(required = false, name = "request-id") String requestId,
            @RequestParam String token,
            @RequestParam String openid) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.wechatLogin(token, openid)));
        body.setMsg("登录成功");
        attachInfo(body);
        logger.info("GET url = api/token/thirdparty/wechat,requestId=" + requestId + "&uid=" + body.getData().getUid() +
                "&token=" + body.getData().getToken() + "&openid=" + openid);
        return body;
    }

    /**
     * 微博登录
     *
     * @param token 微博的token
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/token/thirdparty/weibo", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> weiboLogin(@RequestHeader(required = false, name = "request-id") String requestId,
                                                                   @RequestParam String token) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.weiboLogin(token)));
        body.setMsg("登录成功");
        attachInfo(body);
        logger.info("GET url = api/token/thirdparty/weibo,requestId=" + requestId + "&uid=" + body.getData().getUid());
        return body;
    }

    /**
     * QQ 登录
     *
     * @param token QQ的token
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/token/thirdparty/qq", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> qqLogin(@RequestHeader(required = false, name = "request-id") String requestId,
                                                                @RequestParam String token) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.qqLogin(token)));
        body.setMsg("登录成功");
        attachInfo(body);
        logger.info("GET url = api/token/thirdparty/qq,requestId=" + requestId + "&uid=" + body.getData().getUid());
        return body;
    }

    /**
     * 市民云三方登陆
     *
     * @param requestId
     * @param token     市民云的token
     * @param username  市民云的登录名
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/token/thirdparty/smy", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> smyLogin(@RequestHeader(required = false, name = "request-id") String requestId,
                                                                 @RequestParam String token,
                                                                 @RequestParam String username) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.smyLogin(token, username)));
        body.setMsg("登录成功");
        attachInfo(body);
        logger.info("GET url = api/token/thirdparty/smy,requestId=" + requestId + "&uid=" + body.getData().getUid());
        return body;
    }

    @WithoutToken
    @RequestMapping(value = "/token/thirdparty/jkt", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> jktLogin(@RequestHeader(required = false, name = "request-id") String requestId,
                                                                 @RequestParam String token) {
        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new UserAccountAndSessionDTO(userAccountService.guangzhouLogin(token)));
        body.setMsg("登录成功");
        attachInfo(body);
        logger.info("GET url = api/token/thirdparty/jkt,requestId=" + requestId + "&uid=" + body.getData().getUid());
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
