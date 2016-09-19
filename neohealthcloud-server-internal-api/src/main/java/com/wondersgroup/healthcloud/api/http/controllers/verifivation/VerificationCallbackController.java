package com.wondersgroup.healthcloud.api.http.controllers.verifivation;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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

        if (success) {
            if(from!=522)
                healthRecordUpdateUtil.onVerificationSuccess(info.getPersoncard(),info.getName());
        }

        if(from==522){//儿童实名认证
            AnonymousAccount anonymousAccount = anonymousAccountService.getAnonymousAccount(id,true);
            if(anonymousAccount!=null){
                pushId = anonymousAccount.getCreator();//监护人的Id
                if(success)
                    healthRecordUpdateUtil.onVerificationSuccess(anonymousAccount.getIdcard(),anonymousAccount.getName());
            }
        }

        AppMessage message = AppMessage.Builder.init().title("实名认证")
                .content("您的实名认证已经有结果了, 请点击查看")
                .type(AppMessageUrlUtil.Type.SYSTEM)
                .urlFragment(AppMessageUrlUtil.verificationCallback(pushId, success))
                .persistence().build();
        pushClientWrapper.pushToAlias(message, pushId);

        return "{\"success\":" + success + "}";
    }
}
