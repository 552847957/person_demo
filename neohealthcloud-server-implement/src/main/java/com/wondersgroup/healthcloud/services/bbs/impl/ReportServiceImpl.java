package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.jpa.constant.CommentConstant;
import com.wondersgroup.healthcloud.jpa.constant.ReportConstant;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Report;
import com.wondersgroup.healthcloud.jpa.entity.bbs.ReportDetail;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CommentRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.ReportDetailRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.ReportRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicRepository;
import com.wondersgroup.healthcloud.services.bbs.ReportService;
import com.wondersgroup.healthcloud.services.bbs.criteria.ReportSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.exception.CommentException;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 举报相关
 */
@Service("reportService")
public class ReportServiceImpl implements ReportService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    @Transactional
    @Override
    public Boolean reportTopic(String uid, Integer topicId, Integer reportReason) {
        Topic topic = topicRepository.findOne(topicId);
        if (null == topic || TopicConstant.Status.isDelStatus(topic.getStatus())){
            throw TopicException.deleteForReport();
        }
        return report(topicId, ReportConstant.ReportType.TOPIC, topic.getUid(), uid, reportReason);
    }

    @Override
    public Boolean reportComment(String uid, Integer commentId, Integer reportReason) {
        Comment comment = commentRepository.findOne(commentId);
        if (null == comment || comment.getStatus().intValue() == CommentConstant.Status.DELETE){
            throw CommentException.NotExistForReport();
        }
        return report(commentId, ReportConstant.ReportType.COMMENT, comment.getUid(), uid, reportReason);
    }


    /**
     * 举报
     */
    private Boolean report(Integer targetId, Integer targetType, String targetUid, String uid, Integer reportReason){
        Report report = reportRepository.findReportInfo(targetId, targetType);
        Date nowDate = new Date();
        if (null == report){
            report = new Report();
            report.setUpdateTime(nowDate);
            report.setCreateTime(nowDate);
            report.setFirstReportUid(uid);
            report.setStatus(ReportConstant.ReportStatus.WAIT_REVIEW);
            report.setTargetId(targetId);
            report.setTargetType(targetType);
            report.setTargetUid(targetUid);
            reportRepository.save(report);
            //举报详情
            ReportDetail reportDetail = new ReportDetail();
            reportDetail.setCreateTime(nowDate);
            reportDetail.setReportId(report.getId());
            reportDetail.setUid(uid);
            reportDetail.setReason(reportReason);
            reportDetailRepository.save(reportDetail);
        }else {
            ReportDetail reportDetail = reportDetailRepository.findUserReport(uid, report.getId());
            if (null != reportDetail){
                return true;
            }
            //不在处理 管理员已经处理过的举报
            if (report.getStatus().intValue() != ReportConstant.ReportStatus.WAIT_REVIEW){
                return true;
            }
            //举报详情
            reportDetail = new ReportDetail();
            reportDetail.setCreateTime(nowDate);
            reportDetail.setReportId(report.getId());
            reportDetail.setReason(reportReason);
            reportDetail.setUid(uid);
            reportDetailRepository.save(reportDetail);
            report.setReportCount(report.getReportCount()+1);
            report.setCreateTime(nowDate);
            reportRepository.save(report);
        }
        return true;
    }

    //---------------------------//


    @Override
    public List<Map<String, Object>> getReportTopicListByCriteria(ReportSearchCriteria searchCriteria) {
        return null;
    }

    @Override
    public int countReportTopicByCriteria(ReportSearchCriteria searchCriteria) {
        return 0;
    }

    @Override
    public List<Map<String, Object>> getReportCommentListByCriteria(ReportSearchCriteria searchCriteria) {
        return null;
    }

    @Override
    public int countReportCommentByCriteria(ReportSearchCriteria searchCriteria) {
        return 0;
    }

    @Override
    public Map<String, Object> getReportInfo(Integer reportId) {
        return null;
    }

    @Override
    public Boolean delReportInfo(Integer reportId, String admin_uid) {
        return null;
    }

    @Override
    public Boolean passReportInfo(Integer reportId, String admin_uid) {
        return null;
    }
}
