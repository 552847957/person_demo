package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.CommentConstant;
import com.wondersgroup.healthcloud.jpa.constant.ReportConstant;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.*;
import com.wondersgroup.healthcloud.jpa.repository.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.ReportService;
import com.wondersgroup.healthcloud.services.bbs.TopicVoteService;
import com.wondersgroup.healthcloud.services.bbs.criteria.ReportSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.VoteInfoDto;
import com.wondersgroup.healthcloud.services.bbs.exception.CommentException;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import com.wondersgroup.healthcloud.services.bbs.util.BbsMsgHandler;
import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private TopicContentRepository topicContentRepository;
    @Autowired
    private TopicVoteService topicVoteService;

    @Autowired
    private ReportDetailRepository reportDetailRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BbsMsgHandler bbsMsgHandler;

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
        if (null == comment || comment.getStatus() == CommentConstant.Status.DELETE){
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
            if (report.getStatus() != ReportConstant.ReportStatus.WAIT_REVIEW){
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
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select report.id,report.status, report.report_count, report.create_time, " +
                " topic.title,topic.status as topic_status," +
                " user.nickname, circle.name as circle_name from tb_bbs_report report ");
        querySql.append(" left join tb_bbs_topic topic on topic.id=report.target_id ");
        querySql.append(" left join app_tb_register_info user on user.registerid=report.target_uid ");
        querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
        querySql.append(" where target_type="+ReportConstant.ReportType.TOPIC);

        List<Object> elelmentType = queryParams.getQueryElementType();
        if (StringUtils.isNotEmpty(queryParams.getQueryString())){
            querySql.append(" and " + queryParams.getQueryString());
        }
        querySql.append(searchCriteria.getOrderInfo());
        querySql.append(searchCriteria.getLimitInfo());
        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql.toString(), elelmentType.toArray());
        return list;
    }

    @Override
    public int countReportTopicByCriteria(ReportSearchCriteria searchCriteria) {
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select count(*) from tb_bbs_report report ");
        querySql.append(" left join tb_bbs_topic topic on topic.id=report.target_id ");
        querySql.append(" left join app_tb_register_info user on user.registerid=report.target_uid ");
        querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
        querySql.append(" where target_type="+ReportConstant.ReportType.TOPIC);
        if (StringUtils.isNotEmpty(queryParams.getQueryString())){
            querySql.append(" and  " + queryParams.getQueryString());
        }
        Integer rs = jdbcTemplate.queryForObject(querySql.toString(), queryParams.getQueryElementType().toArray(), Integer.class);
        return rs == null ? 0 : rs;
    }

    @Override
    public List<Map<String, Object>> getReportCommentListByCriteria(ReportSearchCriteria searchCriteria) {
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select report.id,report.status, report.report_count, report.create_time, " +
                "`comment`.content,topic.title,topic.status as topic_status,`comment`.status as comment_status," +
                "user.nickname, circle.name as circle_name from tb_bbs_report report ");
        querySql.append(" left join tb_bbs_comment `comment` on `comment`.id=report.target_id ");
        querySql.append(" left join tb_bbs_topic topic on topic.id=`comment`.topic_id ");
        querySql.append(" left join app_tb_register_info user on user.registerid=report.target_uid ");
        querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
        querySql.append(" where target_type="+ReportConstant.ReportType.COMMENT);

        List<Object> elelmentType = queryParams.getQueryElementType();
        if (StringUtils.isNotEmpty(queryParams.getQueryString())){
            querySql.append(" and " + queryParams.getQueryString());
        }
        querySql.append(searchCriteria.getOrderInfo());
        querySql.append(searchCriteria.getLimitInfo());
        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql.toString(), elelmentType.toArray());
        return list;
    }

    @Override
    public int countReportCommentByCriteria(ReportSearchCriteria searchCriteria) {
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select count(*) from tb_bbs_report report ");
        querySql.append(" left join tb_bbs_comment `comment` on `comment`.id=report.target_id ");
        querySql.append(" left join tb_bbs_topic topic on topic.id=`comment`.topic_id ");
        querySql.append(" left join app_tb_register_info user on user.registerid=report.target_uid ");
        querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
        querySql.append(" where target_type="+ReportConstant.ReportType.COMMENT);
        if (StringUtils.isNotEmpty(queryParams.getQueryString())){
            querySql.append(" and  " + queryParams.getQueryString());
        }
        Integer rs = jdbcTemplate.queryForObject(querySql.toString(), queryParams.getQueryElementType().toArray(), Integer.class);
        return rs == null ? 0 : rs;
    }

    @Override
    public Map<String, Object> getReportInfo(Integer reportId) {
        Report report = reportRepository.findOne(reportId);
        if (report == null){
            throw new CommonException("举报内容无效");
        }
        Map<String, Object> info=null;
        if (report.getTargetType() == ReportConstant.ReportType.TOPIC){
            StringBuffer querySql = new StringBuffer("select report.id,report.status, report.report_count, report.create_time, " +
                    "topic.title,topic.status as topic_status,topic.id as topicId,topic.is_vote," +
                    "user.nickname, circle.name as circle_name from tb_bbs_report report ");
            querySql.append(" left join tb_bbs_topic topic on topic.id=report.target_id ");
            querySql.append(" left join app_tb_register_info user on user.registerid=report.target_uid ");
            querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
            querySql.append(" where report.id="+reportId);
            info = jdbcTemplate.queryForMap(querySql.toString());
            if (null != info){
                Integer topicId = report.getTargetId();
                List<TopicContent> topicContents = topicContentRepository.findContentsByTopicId(topicId);
                info.put("topicContents", topicContents);
                VoteInfoDto voteInfoDto = topicVoteService.getVoteInfoByTopicId(topicId);
                info.put("voteInfo", voteInfoDto);
            }
        }else if (report.getTargetType() == ReportConstant.ReportType.COMMENT){
            StringBuffer querySql = new StringBuffer("select report.id,report.status, report.report_count, `comment`.create_time, " +
                    "`comment`.content,topic.id as topicId, topic.title,topic.status as topic_status,`comment`.status as comment_status,`comment`.floor, " +
                    "user.nickname, circle.name as circle_name from tb_bbs_report report ");
            querySql.append(" left join tb_bbs_comment `comment` on `comment`.id=report.target_id ");
            querySql.append(" left join tb_bbs_topic topic on topic.id=`comment`.topic_id ");
            querySql.append(" left join app_tb_register_info user on user.registerid=report.target_uid ");
            querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
            querySql.append(" where report.id="+reportId);
            info = jdbcTemplate.queryForMap(querySql.toString());
        }
        if (info == null){
            throw new CommonException("举报内容无效");
        }
        String reportDetailSql = "select detail.*, user.nickname from tb_bbs_report_detail detail " +
                " left join app_tb_register_info user on user.registerid=detail.uid " +
                " where detail.report_id="+reportId + " order by detail.id asc ";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(reportDetailSql);
        if (list != null){
            Map<Integer, String> reasonList = ReportConstant.ReportReason.reasonList;
            for (Map<String, Object> map : list){
                int reasonInt = Integer.parseInt(map.get("reason").toString());
                map.put("reason", reasonList.containsKey(reasonInt) ? reasonList.get(reasonInt) : "");
            }
        }
        info.put("report_list", list);
        return info;
    }

    @Transactional
    @Override
    public Boolean delReportInfo(Integer reportId, String admin_uid) {
        Report report = reportRepository.findOne(reportId);
        if (null == report){
            throw new CommonException("举报无效");
        }
        report.setStatus(ReportConstant.ReportStatus.DEL_TARGET);
        Date nowDate = new Date();
        report.setUpdateTime(nowDate);
        reportRepository.save(report);
        //这里要处理掉
        if (report.getTargetType() == ReportConstant.ReportType.TOPIC){
            Topic topic = topicRepository.findOne(report.getTargetId());
            topic.setStatus(TopicConstant.Status.ADMIN_DELETE);
            topic.setUpdateTime(nowDate);
            topicRepository.save(topic);
            bbsMsgHandler.adminDelTopic(topic.getUid(), topic.getId());
        }else if (report.getTargetType() == ReportConstant.ReportType.COMMENT){
            Comment comment = commentRepository.findOne(report.getTargetId());
            comment.setStatus(CommentConstant.Status.DELETE);
            comment.setUpdateTime(nowDate);
            commentRepository.save(comment);
            //通知用户
            bbsMsgHandler.adminDelComment(admin_uid, comment.getId());
        }
        return true;
    }



    @Override
    public Boolean passReportInfo(Integer reportId, String admin_uid) {
        Report report = reportRepository.findOne(reportId);
        if (null == report){
            throw new CommonException("举报无效");
        }
        report.setStatus(ReportConstant.ReportStatus.SET_OK);
        report.setUpdateTime(new Date());
        reportRepository.save(report);
        return true;
    }
}
