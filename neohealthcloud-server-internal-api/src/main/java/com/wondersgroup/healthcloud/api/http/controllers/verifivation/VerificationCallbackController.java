package com.wondersgroup.healthcloud.api.http.controllers.verifivation;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.helper.healthrecord.HealthRecordUpdateUtil;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.helper.push.api.PushClientWrapper;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.user.AnonymousAccountService;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Created by longshasha on 16/8/21.
 */
@RestController
@RequestMapping("/api")
public class VerificationCallbackController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AnonymousAccountService anonymousAccountService;

    @Autowired
    private PushClientWrapper pushClientWrapper;

    @Autowired
    private HealthRecordUpdateUtil healthRecordUpdateUtil;

    private static final Logger logger = LoggerFactory.getLogger(VerificationCallbackController.class);

    @RequestMapping(value = "/verification/callback", method = RequestMethod.GET)
    public String wondersCloudAPICallback(@RequestParam(value = "id") String id,
                                          @RequestParam(value = "from", required = false) Integer from,
                                          @RequestParam(value = "success") Boolean success,
                                          @RequestParam(value = "msg", required = false) String msg) throws Exception {
        String decodedMsg = "";
        logger.info(String.format("callback_api_id:[%s],success:[%s],msg:[%s]", id, success.toString(), decodedMsg));

        RegisterInfo info = userAccountService.fetchInfo(id);

        String pushId = id;
        Integer type = 1;
        String idCard = "";
        String name = "";
        String title = "实名认证";
        String content = "您的实名认证已经有结果了,请点击查看";


        if (info != null) {
            idCard = info.getPersoncard();
            name = info.getName();
        } else {
            AnonymousAccount anonymousAccount = anonymousAccountService.getAnonymousAccount(id, true);
            if (anonymousAccount != null) {
                pushId = anonymousAccount.getCreator();//监护人的Id
                idCard = anonymousAccount.getIdcard();
                name = anonymousAccount.getName();
                if (from == 522 || anonymousAccount.getIsChild()) {//儿童实名认证
                    title = "儿童实名认证";
                    type = 3;
                } else {
                    title = "家庭账户实名认证";
                    type = 2;
                }
                content = "您的家庭实名认证结果已经被处理,请查看";
            }

        }

        AppMessage message = AppMessage.Builder.init().title(title)
                .content(content)
                .type(AppMessageUrlUtil.Type.SYSTEM)
                .urlFragment(AppMessageUrlUtil.verificationCallback(id, success, type))
                .persistence().build();
        pushClientWrapper.pushToAlias(message, pushId);

        if (success) {
            healthRecordUpdateUtil.onVerificationSuccess(idCard, name);
        }
        return "{\"success\":" + success + "}";
    }
}
