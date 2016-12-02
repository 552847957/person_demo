package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.constant.ReportConstant;
import com.wondersgroup.healthcloud.services.bbs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 *  1. 话题举报
 *  2. 评论举报
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 举报原因
     */
    @RequestMapping(value = "/reasonList", method = RequestMethod.GET)
    public JsonResponseEntity<Map<Integer, String>> reasonList(){
        JsonResponseEntity<Map<Integer, String>> rt = new JsonResponseEntity();
        Map<Integer, String> reasonList = ReportConstant.ReportReason.reasonList;
        rt.setData(reasonList);
        return rt;
    }

    /**
     * 话题举报
     */
    @RequestMapping(value = "/topicReport", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> topicReport(@RequestBody String request){
        JsonResponseEntity<Map<String, Object>> rt = new JsonResponseEntity();

        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        Integer topicId = reader.readInteger("topicId", false);
        Integer reportReason = reader.readDefaultInteger("reportReason", 0);
        Boolean isOK = reportService.reportTopic(uid, topicId, reportReason);
        rt.setMsg(isOK ? "举报成功" : "举报失败!");
        return rt;
    }

    /**
     * 评论举报
     */
    @RequestMapping(value = "/commentReport", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> commentReport(@RequestBody String request){
        JsonResponseEntity<Map<String, Object>> rt = new JsonResponseEntity();

        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        Integer commentId = reader.readInteger("commentId", false);
        Integer reportReason = reader.readDefaultInteger("reportReason", 0);
        Boolean isOK = reportService.reportComment(uid, commentId, reportReason);
        rt.setMsg(isOK ? "举报成功" : "举报失败!");
        return rt;
    }

}
