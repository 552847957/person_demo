package com.wondersgroup.healthcloud.services.medicalcircle.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleAttach;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleTransmit;
import com.wondersgroup.healthcloud.jpa.entity.circle.CircleReport;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircle;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleAttention;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleCollect;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleCommunity;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleReply;
import com.wondersgroup.healthcloud.jpa.repository.circle.ArticleAttachRepository;
import com.wondersgroup.healthcloud.jpa.repository.circle.ArticleTransmitRepository;
import com.wondersgroup.healthcloud.jpa.repository.circle.CircleReportRepository;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleAttentionRepository;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleCollectRepository;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleCommunityRepository;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleReplyRepository;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleRepository;
import com.wondersgroup.healthcloud.services.medicalcircle.CircleService;
import com.wondersgroup.healthcloud.services.medicalcircle.MedicalCircleService;
import com.wondersgroup.healthcloud.utils.PageFactory;
import com.wondersgroup.healthcloud.utils.circle.CircleLikeUtils;
import com.wondersgroup.healthcloud.utils.circle.CircleReportUtils;
import com.wondersgroup.healthcloud.utils.circle.CircleViewsUtils;

@Service
public class MedicalCircleServiceImpl implements MedicalCircleService {

    @Autowired
    private MedicalCircleCommunityRepository mcCommunityRepo;
    @Autowired
    private MedicalCircleReplyRepository mcReplyRepo;
    @Autowired
    private MedicalCircleRepository mcRepo;
    @Autowired
    private MedicalCircleCollectRepository mcCollectRepo;
    @Autowired
    private MedicalCircleAttentionRepository mcAttentionRepo;
    @Autowired
    private ArticleAttachRepository articleAttachRepository;
    @Autowired
    private ArticleTransmitRepository articleTransmitRepository;
    @Autowired
    private CircleService circleService;
    @Autowired
    private CircleReportRepository circleReportRepository;
    @Autowired
    private CircleLikeUtils circleLikeUtils;
    @Autowired
    private CircleReportUtils circleReportUtils;
    @Autowired
    private CircleViewsUtils circleViewsUtils;

    @Override
    public List<MedicalCircle> getAllMedicalCircle(Integer[] type,String order, Date flag) {
        return mcRepo.findAllMedicalCircle(type,flag, PageFactory.create(1, 20, order)).getContent();
    }

    @Override
    public MedicalCircle getMedicalCircle(String circleId) {
        return mcRepo.findOne(circleId);
    }

    @Override
    public List<MedicalCircle> getUserMedicalCircle(String doctorId, Integer[] type,Date flag, String order) {
        return mcRepo.findUserMedicalCircle(doctorId, type, flag, PageFactory.create(1, 20, order)).getContent();
    }

    @Override
    public MedicalCircle getUserMedicalCircleNewest(String doctorId, Integer[] type) {
        List<MedicalCircle> list = mcRepo.findUserMedicalCircleNewest(doctorId, type, PageFactory.create(1, 1, "sendtime:desc")).getContent();
        if (null == list || list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    @Override
    public Long getCommentsNum(String circleId) {
        return mcCommunityRepo.getCommentNum(circleId);
    }

    @Override
    public MedicalCircleCommunity comment(String doctorId, String circleId, String content) {
        MedicalCircleCommunity comm = new MedicalCircleCommunity();
        comm.setCircleid(circleId);
        comm.setContent(content);
        comm.setDelFlag("0");
        comm.setDiscusstime(new Date());
        comm.setDoctorid(doctorId);
        comm.setId(IdGen.uuid());
        comm.setIsreply(false);
        comm.setCreateBy(doctorId);
        comm.setCreateDate(new Date());
        comm.setUpdateDate(new Date());
        comm.setUpdateBy(doctorId);
        return mcCommunityRepo.save(comm);
    }

    @Override
    public MedicalCircleReply reply(String commentId,String doctorId, String replyId, String content){
        MedicalCircleReply reply = new MedicalCircleReply();
        reply.setId(IdGen.uuid());
        reply.setDoctorid(doctorId);
        reply.setCommunityid(commentId);
        reply.setContent(content);
        reply.setReplyid(replyId);
        reply.setDiscusstime(new Date());
        reply.setCreateBy(replyId);
        reply.setCreateDate(new Date());
        reply.setUpdateBy(replyId);
        reply.setUpdateDate(new Date());
        reply.setDelFlag("0");
        MedicalCircleCommunity comment = mcCommunityRepo.findOne(commentId);
        comment.setIsreply(true);
        mcCommunityRepo.save(comment);
        return mcReplyRepo.save(reply);
    }

    @Override
    public Boolean like(String doctorId, String circleId) {
        Boolean success = circleLikeUtils.likeOne(circleId, doctorId);
        if(success){
            MedicalCircle mc = mcRepo.findOne(circleId);
            mc.setPraisenum(mc.getPraisenum() == null ? 1 : mc.getPraisenum() + 1);
            mcRepo.save(mc);
        }
        return success;
    }

    @Override
    public Boolean unlike(String doctorId, String circleId) {
        MedicalCircle mc = mcRepo.findOne(circleId);
        Long praisenum = mc.getPraisenum();
        if(praisenum<=0){
            return false;
        }
        Boolean success = circleLikeUtils.cancelLikeOne(circleId, doctorId);
        if(success) {
            mc.setPraisenum(mc.getPraisenum() - 1);
            mcRepo.save(mc);
        }
        return success;
    }

    @Override
    public void publish(String doctorId, String title, String content, Integer circleType, List<String> imageUrls) {
        MedicalCircle mc = saveMedicalCircle(doctorId, title, content, circleType);
        if(imageUrls!=null&&imageUrls.size()>0){
            saveArticleAttach(mc, imageUrls);
        }
        mcRepo.save(mc);
    }

    private MedicalCircle saveMedicalCircle(String doctorId,String title,String content,Integer circleType){
        MedicalCircle mc = new MedicalCircle();
        mc.setId(IdGen.uuid());
        mc.setTitle(title);
        mc.setDoctorid(doctorId);
        mc.setCreateBy(doctorId);
        mc.setCreateDate(new Date());
        mc.setUpdateBy(doctorId);
        mc.setUpdateDate(new Date());
        mc.setDelFlag("0");
        mc.setContent(content);
        mc.setType(circleType);
        mc.setSendtime(new Date());
        return mc;
    }


    private void saveArticleAttach(MedicalCircle circle,List<String> imageURLs){
        for(int i=0;i<imageURLs.size();i++){
            ArticleAttach articleAttach = new ArticleAttach();
            articleAttach.setId(IdGen.uuid());
            articleAttach.setArticleid(circle.getId());
            articleAttach.setAttachid(imageURLs.get(i));
            articleAttach.setAttachtype(1);
            articleAttach.setSort(i);
            articleAttach.setCreateBy(circle.getDoctorid());
            articleAttach.setCreateDate(new Date());
            articleAttach.setUpdateDate(new Date());
            articleAttach.setUpdateBy(circle.getDoctorid());
            articleAttachRepository.save(articleAttach);
        }
    }

    @Override
    public List<MedicalCircleCommunity> getMedicalCircleComments(String circleId, String order, Date flag) {
        return  mcCommunityRepo.findCommentsList(circleId,flag,PageFactory.create(1, 20, order)).getContent();
    }

    @Override
    public List<ArticleAttach> getCircleAttachs(String circleId) {
        return circleService.getArticleAttachs(circleId);
    }

    @Override
    public void forward(String doctorId, String title, String desc, String thumb, String url, String content) {
        MedicalCircle mc = saveMedicalCircle(doctorId, null, content, 3);
        ArticleTransmit articleTransmit = circleService.saveArticleTransmit(title, desc, thumb, url);
        mc.setTransmitid(articleTransmit.getId());
        mcRepo.save(mc);
    }

    @Override
    public ArticleTransmit getMedicalCircleForward(String circleId) {
        MedicalCircle circle = mcRepo.findOne(circleId);
        if(null!=circle){
            String transmitid = circle.getTransmitid();
            if(StringUtils.isNotEmpty(transmitid)){
                return articleTransmitRepository.findOne(transmitid);
            }
        }
        return null;
    }

    @Override
    public Boolean report(String uid, String reportId, Integer contentType, Integer reportType) {
        Assert.isTrue(contentType == 1 || contentType == 2, "接收参数值：" + contentType + "，举报类型错误(1.帖子 2.评论)");
        Assert.isTrue(reportType>=1&&reportType<=5,"接收参数值："+reportType+"，举报理由错误(1.色情 2.政治 3.骂人 4.广告 5.其他)");
        Boolean success = circleReportUtils.reportOne(reportId, uid, String.valueOf(contentType));
        if(success){
            saveReport(uid, reportId, contentType, reportType);
        }
        return success;
    }

    private void saveReport(String uid, String reportId,Integer contentType,Integer reportType){
        CircleReport report = circleReportRepository.findByReportid(reportId);
        if(report==null){
            String reportContent = "";
            String reportContentUserId = "";
            if (contentType == 1){
                //文章
                MedicalCircle mc = mcRepo.findOne(reportId);
                if(mc!=null){
                    reportContent =  mc.getContent();
                    reportContentUserId = mc.getDoctorid();
                }
            }else if (contentType == 2){
                //评论
                MedicalCircleCommunity mcCommunity = mcCommunityRepo.findOne(reportId);
                MedicalCircleReply mcReply = mcReplyRepo.findOne(reportId);
                if(mcCommunity!=null){
                    reportContent = mcCommunity.getContent();
                    reportContentUserId = mcCommunity.getDoctorid();
                }
                if(mcReply!=null){
                    reportContent = mcReply.getContent();
                    reportContentUserId = mcReply.getDoctorid();
                }
            }
            CircleReport circleReport = new CircleReport();
            circleReport.setReportid(reportId);
            circleReport.setContent(reportContent);
            circleReport.setRegisterid(reportContentUserId);
            circleReport.setReportnum(1);
            circleReport.setReporttime(new Date());
            circleReport.setReporttype(reportType);
            circleReport.setContenttype(contentType);
            circleReport.setCreateDate(new Date());
            circleReport.setCreateBy(uid);
            circleReport.setUpdateDate(new Date());
            circleReport.setDealstatus("0");
            circleReport.setDelFlag("0");
            circleReportRepository.save(circleReport);
        }else{
            report.setReportnum(report.getReportnum()+1);
            report.setUpdateDate(new Date());
            circleReportRepository.save(report);
        }
    }

    @Override
    public Boolean delMedicalCircle(String doctorId, String circleId) {
        MedicalCircle mc = mcRepo.findMedicalCircleWithUser(doctorId, circleId);
        if(mc!=null){
            mc.setDelFlag("1");
            mcRepo.save(mc);
            return true;
        }
        return false;
    }

    @Override
    public Boolean delComment(String doctorId, String commentId) {
        MedicalCircleCommunity community = mcCommunityRepo.getCommunityWithUser(commentId, doctorId);
        if(community!=null){
            community.setDelFlag("1");
            mcCommunityRepo.save(community);
            return true;
        }
        return false;
    }

    @Override
    public Boolean delReply(String replyId,String replyDoctorId){
        MedicalCircleReply reply = mcReplyRepo.findReplyWithUser(replyId, replyDoctorId);
        if(reply!=null){
            reply.setDelFlag("1");
            mcReplyRepo.save(reply);
            return true;
        }
        return false;
    }

    @Override
    public Boolean collect(String circleId, String doctorId,Integer type){
        MedicalCircleCollect collect = mcCollectRepo.findCollectByDoctor(circleId, doctorId, type);
        if(collect==null){
            collect = new MedicalCircleCollect();
            collect.setId(IdGen.uuid());
            collect.setDoctorid(doctorId);
            collect.setCircleid(circleId);
            collect.setType(type);
            collect.setCollecttime(new Date());
            collect.setCreateBy(doctorId);
            collect.setCreateDate(new Date());
            collect.setUpdateBy(doctorId);
            collect.setUpdateDate(new Date());
            collect.setDelFlag("0");
            mcCollectRepo.save(collect);
            return true;
        }
        return false;
    }



    @Override
    public Boolean delCollect(String circleId, String doctorId,Integer type){
        MedicalCircleCollect collect = mcCollectRepo.findCollectByDoctor(circleId, doctorId, type);
        if(collect != null){
            mcCollectRepo.delete(collect);
        }
        return true;
    }

    @Override
    public Boolean checkCollect(String circleId, String doctorId,Integer type){
        MedicalCircleCollect collect = mcCollectRepo.findCollectByDoctor(circleId, doctorId, type);
        return null == collect || collect.getDelFlag().equals("1") ? false : true;
    }

    @Override
    public Boolean attention(String attentionId,String followedId){
        MedicalCircleAttention attention = mcAttentionRepo.findAttention(attentionId, followedId);
        if(attention==null){
            attention = new MedicalCircleAttention();
            attention.setId(IdGen.uuid());
            attention.setAttentiontime(new Date());
            attention.setDoctorid(attentionId);
            attention.setConcernedid(followedId);
            attention.setCreateBy(attentionId);
            attention.setCreateDate(new Date());
            attention.setUpdateBy(followedId);
            attention.setUpdateDate(new Date());
            attention.setDelFlag("0");
            mcAttentionRepo.save(attention);
            return true;
        }
        return false;
    }

    @Override
    public Boolean cancelAttention(String attentionId,String followedId){
        MedicalCircleAttention attention = mcAttentionRepo.findAttention(attentionId, followedId);
        if(attention!=null){
            mcAttentionRepo.delete(attention);
            return true;
        }
        return false;
    }

    @Override
    public Long getDocFollowedNum(String doctorId) {
        return mcAttentionRepo.getAttentionNum(doctorId);
    }

    @Override
    public Long getDocFansNum(String doctorId){
       return mcAttentionRepo.getFansNum(doctorId);
    }

    @Override
    public List<MedicalCircleAttention> getDocFollowedList(String doctorId,String order,Date flag){
        return mcAttentionRepo.findAttentionList(doctorId,flag,
                PageFactory.create(1, 20,order)).getContent();
    }

    private List<String> getDocIdListFromAttention(List<MedicalCircleAttention> list){
        List<String> result = Lists.newArrayList();
        for (MedicalCircleAttention att : list) {
            result.add(att.getDoctorid());
        }
        return result;
    }

    @Override
    public List<MedicalCircleAttention> getDocFansList(String doctorId, String order,Date flag){
        return mcAttentionRepo.findFansList(doctorId,flag,
                PageFactory.create(1, 20,order)).getContent();
    }

    @Override
    public void view(String circleId, String doctorId){
        circleViewsUtils.viewOne(circleId, doctorId);
    }

    @Override
    public Long getCircleViews(String circleId){
        return circleViewsUtils.totalViews(circleId) == 0 ? 1 : circleViewsUtils.totalViews(circleId);
    }

    @Override
    public List<MedicalCircleReply> getCommentReplyList(String commentId, Date flag, String order ,Integer pageSize){
        return mcReplyRepo.findCommentReplyList(commentId,flag,PageFactory.create(1,pageSize,order)).getContent();
    }

    @Override
    public String getFloor(Integer floor){
        if(floor==1){
            return "沙发";
        }else if(floor==2){
            return "板凳";
        }else{
            return floor+"楼";
        }
    }

    @Override
    public Boolean isAttention(String attentionId, String followedId){
        MedicalCircleAttention attention = mcAttentionRepo.findAttention(attentionId, followedId);
        return attention != null;
    }


    @Override
    public Long getDynamicNum(String doctorId){
        return mcRepo.getCircleNum(Lists.newArrayList(3),doctorId);
    }

    @Override
    public Long getNoteCaseNum(String doctorId){
        return mcRepo.getCircleNum(Lists.newArrayList(1, 2),doctorId);
    }


    @Override
    public List<Map<String,Integer>> getCollectCircleList(String doctorId,Date flag,String order){
        List<Map<String,Integer>> ids = new ArrayList<>();
        List<MedicalCircleCollect> collectList = mcCollectRepo.findCollectList(doctorId, flag, PageFactory.create(1, 20, order)).getContent();
        for (MedicalCircleCollect collect : collectList) {
            Map<String,Integer> map = new HashMap();
            map.put(collect.getCircleid(), collect.getType());
            ids.add(map);
        }
        return ids;
    }

    @Override
    public List<MedicalCircle> getCollectCircleList(String doctorId, Date flag, Integer[] type) {
        return mcRepo.findUserCollectMedicalCircle(doctorId, type, flag, PageFactory.create(1, 20)).getContent();
    }

    @Override
    public List<MedicalCircleCollect> getCollectCircleListByType(String doctorId,int type, Date flag,String order){
        List<MedicalCircleCollect> collectList = mcCollectRepo.findCollectListByType(doctorId, type, flag, PageFactory.create(1, 20, order)).getContent();
        return collectList;
    }
}
