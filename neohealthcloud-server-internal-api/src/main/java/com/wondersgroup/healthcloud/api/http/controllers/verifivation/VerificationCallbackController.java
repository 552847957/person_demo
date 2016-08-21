package com.wondersgroup.healthcloud.api.http.controllers.verifivation;

import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.helper.push.api.PushClientWrapper;
import com.wondersgroup.healthcloud.helper.push.area.PushClientSelector;
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
    private PushClientWrapper pushClientWrapper;

    private static final Logger logger = LoggerFactory.getLogger(VerificationCallbackController.class);

    @RequestMapping(value = "/verification/callback", method = RequestMethod.GET)
    public String wondersCloudAPICallback(@RequestParam(value = "id") String id,
                                          @RequestParam(value = "from", required = false) Integer from,
                                          @RequestParam(value = "success") Boolean success,
                                          @RequestParam(value = "msg", required = false) String msg) throws Exception {
        String decodedMsg = "";
        logger.info(String.format("callback_api_id:[%s],success:[%s],msg:[%s]", id, success.toString(), decodedMsg));

        userAccountService.fetchInfo(id);

        AppMessage message = AppMessage.Builder.init().title("实名认证")
                .content("您的实名认证已经有结果了, 请点击查看")
                .type(AppMessageUrlUtil.Type.SYSTEM)
                .urlFragment(AppMessageUrlUtil.verificationCallback(success))
                .persistence(true).build();
        pushClientWrapper.pushToAlias(message, id);

        return "{\"success\":" + success + "}";
    }
}
