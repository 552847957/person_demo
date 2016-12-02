package com.wondersgroup.healthcloud.services.bbs;

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

}
