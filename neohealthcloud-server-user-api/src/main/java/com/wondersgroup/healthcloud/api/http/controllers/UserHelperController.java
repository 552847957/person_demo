package com.wondersgroup.healthcloud.api.http.controllers;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.user.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class UserHelperController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 意见反馈
     *
     * @return
     */
    @RequestMapping(value = "/user/feedback", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> saveFeedback(@RequestBody String request) {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", true);
        String comments = reader.readString("comments", false);
        String contact = reader.readString("contact", true);

        feedbackService.saveFeedback(uid, comments, contact, "0");
        response.setData("反馈成功");
        return response;
    }

}
