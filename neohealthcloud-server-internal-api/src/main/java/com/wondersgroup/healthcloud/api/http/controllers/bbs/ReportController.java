package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.constant.ReportConstant;
import com.wondersgroup.healthcloud.services.bbs.ReportService;
import com.wondersgroup.healthcloud.services.bbs.criteria.ReportSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Admin
    @RequestMapping(value = "/topic/list", method = RequestMethod.POST)
    public Pager topicList(@RequestBody Pager pager, @RequestHeader String appUid){
        Map<String, Object> parms = pager.getParameter();
        ReportSearchCriteria searchCriteria = new ReportSearchCriteria(parms);
        searchCriteria.setPage(pager.getNumber());
        searchCriteria.setPageSize(pager.getSize());
        searchCriteria.setOrderInfo("report.create_time desc");
        int totalSize = reportService.countReportTopicByCriteria(searchCriteria);

        List<Map<String, Object>> list= reportService.getReportTopicListByCriteria(searchCriteria);
        pager.setTotalElements(totalSize);
        pager.setData(list);
        return pager;
    }

    @Admin
    @RequestMapping(value = "/topic/view", method = RequestMethod.GET)
    public JsonResponseEntity<Map<String, Object>> topicView(@RequestHeader String appUid, @RequestParam Integer id){
        JsonResponseEntity<Map<String, Object>> entity = new JsonResponseEntity();
        Map<String, Object> info = reportService.getReportInfo(id);
        entity.setData(info);
        return entity;
    }

    @Admin
    @RequestMapping(value = "/pass", method = RequestMethod.GET)
    public JsonResponseEntity<Map<String, Object>> pass(@RequestHeader String appUid, @RequestParam Integer id){
        JsonResponseEntity<Map<String, Object>> entity = new JsonResponseEntity();
        Boolean isOk = reportService.passReportInfo(id, appUid);
        entity.setMsg(isOk ? "操作成功!" : "操作失败");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/del", method = RequestMethod.GET)
    public JsonResponseEntity<Map<String, Object>> del(@RequestHeader String appUid, @RequestParam Integer id){
        JsonResponseEntity<Map<String, Object>> entity = new JsonResponseEntity();
        Boolean isOk = reportService.delReportInfo(id, appUid);
        entity.setMsg(isOk ? "删除成功!" : "删除失败");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/comment/list", method = RequestMethod.POST)
    public Pager commentList(@RequestBody Pager pager, @RequestHeader String appUid){
        Map<String, Object> parms = pager.getParameter();
        ReportSearchCriteria searchCriteria = new ReportSearchCriteria(parms);
        searchCriteria.setPage(pager.getNumber());
        searchCriteria.setPageSize(pager.getSize());
        searchCriteria.setOrderInfo("report.create_time desc");
        int totalSize = reportService.countReportCommentByCriteria(searchCriteria);

        List<Map<String, Object>> list= reportService.getReportCommentListByCriteria(searchCriteria);
        pager.setTotalElements(totalSize);
        pager.setData(list);
        return pager;
    }

    @Admin
    @RequestMapping(value = "/reasonList", method = RequestMethod.GET)
    public JsonResponseEntity<Map<Integer, String>> reasonList(){
        JsonResponseEntity<Map<Integer, String>> rt = new JsonResponseEntity();
        Map<Integer, String> reasonList = ReportConstant.ReportReason.reasonList;
        rt.setData(reasonList);
        return rt;
    }

}
