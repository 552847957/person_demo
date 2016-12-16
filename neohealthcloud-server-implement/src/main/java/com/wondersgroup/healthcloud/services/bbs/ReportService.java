package com.wondersgroup.healthcloud.services.bbs;

import com.wondersgroup.healthcloud.services.bbs.criteria.ReportSearchCriteria;

import java.util.List;
import java.util.Map;

/**
 * Created by ys on 2016/09/13.
 * <p>
 * 举报相关
 * </p>
 * @author ys
 */
public interface ReportService {

    /**
     * 话题举报
     */
    Boolean reportTopic(String uid, Integer topicId, Integer reportReason);

    /**
     * 评论举报
     */
    Boolean reportComment(String uid, Integer commentId, Integer reportReason);


    //---------------------------管理后台------------------------//

    List<Map<String, Object>> getReportTopicListByCriteria(ReportSearchCriteria searchCriteria);

    int countReportTopicByCriteria(ReportSearchCriteria searchCriteria);

    List<Map<String, Object>> getReportCommentListByCriteria(ReportSearchCriteria searchCriteria);

    int countReportCommentByCriteria(ReportSearchCriteria searchCriteria);

    Map<String, Object> getReportInfo(Integer reportId);

    Boolean delReportInfo(Integer reportId, String admin_uid);

    Boolean passReportInfo(Integer reportId, String admin_uid);


}
