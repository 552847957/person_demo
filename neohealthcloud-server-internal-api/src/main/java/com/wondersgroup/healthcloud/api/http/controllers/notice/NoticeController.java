package com.wondersgroup.healthcloud.api.http.controllers.notice;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.notice.Notice;
import com.wondersgroup.healthcloud.services.notice.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhaozhenxing on 2016/8/18.
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Admin
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponseEntity list(@RequestHeader(name = "main-area", required = true) String mainArea,
                                   @RequestHeader(name = "spec-area", required = false) String specArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        List<Notice> noticeList = noticeService.findAllNoticeByArea(mainArea, specArea);
        if (noticeList != null && noticeList.size() > 0) {
            result.setData(noticeList);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关配置数据！");
        }
        return result;
    }

    @Admin
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public JsonResponseEntity info(@RequestParam(required = true) String id) {
        JsonResponseEntity result = new JsonResponseEntity();
        Notice notice = noticeService.findNoticeByid(id);
        if (notice != null) {
            result.setData(notice);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关配置数据！");
        }
        return result;
    }


    @Admin
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public JsonResponseEntity save(@RequestBody Notice notice) {
        JsonResponseEntity result = new JsonResponseEntity();
        Notice rtnNotice = noticeService.saveNotice(notice);
        if (rtnNotice != null) {
            result.setMsg("配置信息保存成功！");
        } else {
            result.setCode(1000);
            result.setMsg("配置信息保存失败！");
        }
        return result;
    }
}
