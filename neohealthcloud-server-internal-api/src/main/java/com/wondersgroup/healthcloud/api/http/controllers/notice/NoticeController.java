package com.wondersgroup.healthcloud.api.http.controllers.notice;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.notice.Notice;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zhaozhenxing on 2016/8/18.
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Admin
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponseEntity list(@RequestParam(required = true) String mainArea,
                                   @RequestParam(required = false) String specArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        return result;
    }

    @Admin
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public JsonResponseEntity info(@RequestParam(required = true) String mainArea,
                                   @RequestParam(required = false) String specArea,
                                   @RequestParam(required = true) String id) {
        JsonResponseEntity result = new JsonResponseEntity();

        return result;
    }


    @Admin
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public JsonResponseEntity save(@RequestBody Notice notice) {
        JsonResponseEntity result = new JsonResponseEntity();

        return result;
    }
}
