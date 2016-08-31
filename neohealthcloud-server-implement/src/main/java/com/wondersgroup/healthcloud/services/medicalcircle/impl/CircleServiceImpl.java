package com.wondersgroup.healthcloud.services.medicalcircle.impl;


import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleAttach;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleTransmit;
import com.wondersgroup.healthcloud.jpa.entity.circle.CircleReport;
import com.wondersgroup.healthcloud.jpa.entity.circle.CommunityDiscuss;
import com.wondersgroup.healthcloud.jpa.entity.circle.HealthCircle;
import com.wondersgroup.healthcloud.jpa.repository.circle.ArticleAttachRepository;
import com.wondersgroup.healthcloud.jpa.repository.circle.ArticleTransmitRepository;
import com.wondersgroup.healthcloud.jpa.repository.circle.CircleReportRepository;
import com.wondersgroup.healthcloud.jpa.repository.circle.CommunityDiscussRepository;
import com.wondersgroup.healthcloud.jpa.repository.circle.HealthCircleRepository;
import com.wondersgroup.healthcloud.services.medicalcircle.CircleService;
import com.wondersgroup.healthcloud.utils.PageFactory;
import com.wondersgroup.healthcloud.utils.circle.CircleLikeUtils;
import com.wondersgroup.healthcloud.utils.circle.CircleReportUtils;

/**
 */
@Service
public class CircleServiceImpl implements CircleService {

    @Autowired
    private HealthCircleRepository healthCircleRepository;
    @Autowired
    private CommunityDiscussRepository communityDiscussRepository;
    @Autowired
    private ArticleAttachRepository articleAttachRepository;
    @Autowired
    private ArticleTransmitRepository articleTransmitRepository;
    @Autowired
    private CircleReportRepository circleReportRepository;

    @Override
    public List<HealthCircle> getHotArticles(String flag,Integer num,Float praiseNum) {
        String time = new DateTime().plusDays(-2).toString("yyyy-MM-dd HH:mm:ss");
        if(num==null&&praiseNum==null){
            return healthCircleRepository.findHotCircle(time,flag);
        }
        return healthCircleRepository.findHotCircle(time, flag, num, praiseNum);
    }

    @Override
    public List<HealthCircle> getAllArticles(String order,Date flag) {
        return healthCircleRepository.findAllCircle(flag,PageFactory.create(1, 15 , order)).getContent();
    }

    @Override
    public HealthCircle getArticle(String articleId) {
        return healthCircleRepository.findOne(articleId);
    }

    @Override
    public List<HealthCircle> getUserArticles(String registerId,Date flag,String order) {
        return healthCircleRepository.findUserCircle(registerId, flag, PageFactory.create(1, 15, order)).getContent();
    }

    @Override
    public Long getCommentsNum(String articleId) {
        return communityDiscussRepository.findByArticleId(articleId, PageFactory.create(1, 15)).getTotalElements();
    }

    @Override
    public void comment(String registerId, String articleId, String content) {
        CommunityDiscuss communityDiscuss = new CommunityDiscuss();
        communityDiscuss.setId(IdGen.uuid());
        communityDiscuss.setRegisterid(registerId);
        communityDiscuss.setContent(content);
        communityDiscuss.setArticleid(articleId);
        communityDiscuss.setDiscusstime(new Date());
        communityDiscuss.setUpdateDate(new Date());
        communityDiscuss.setUpdateBy(registerId);
        communityDiscuss.setCreateDate(new Date());
        communityDiscuss.setCreateBy(registerId);
        communityDiscussRepository.save(communityDiscuss);
    }

    @Override
    public Boolean like(String registerId, String articleId) {
        Boolean success = CircleLikeUtils.likeOne(articleId, registerId);
        if(success){
            HealthCircle article = healthCircleRepository.findOne(articleId);
            article.setPraisenum(article.getPraisenum() == null ? 1 : article.getPraisenum() + 1);
            healthCircleRepository.save(article);
        }
        return success;
    }

    @Override
    public Boolean unlike(String registerId, String articleId) {
        Boolean success = CircleLikeUtils.cancelLikeOne(articleId, registerId);
        if(success) {
            HealthCircle article = healthCircleRepository.findOne(articleId);
            article.setPraisenum(article.getPraisenum() - 1);
            healthCircleRepository.save(article);
        }
        return success;
    }

    @Override
    public void publish(String registerId, String content) {
        saveArticle(registerId, content);
    }

    @Override
    public void publishWithFiles(String registerId, String content, List<String> fileURLs,Integer fileType) {
        saveArticleAttach(saveArticle(registerId, content), fileURLs, fileType);
    }

    private void saveArticleAttach(HealthCircle circle,List<String> fileURLs,Integer fileType){
        Assert.notNull(circle);
        for(int i=0;i<fileURLs.size();i++){
            ArticleAttach articleAttach = new ArticleAttach();
            articleAttach.setId(IdGen.uuid());
            articleAttach.setArticleid(circle.getId());
            articleAttach.setAttachid(fileURLs.get(i));
            articleAttach.setAttachtype(fileType);
            articleAttach.setSort(i);
            articleAttach.setCreateBy(circle.getRegisterid());
            articleAttach.setCreateDate(new Date());
            articleAttach.setUpdateDate(new Date());
            articleAttach.setUpdateBy(circle.getRegisterid());
            articleAttachRepository.save(articleAttach);
        }
    }

    private HealthCircle saveArticle(String registerId,String content){
        HealthCircle healthCircle = new HealthCircle();
        String articleId = IdGen.uuid();
        healthCircle.setId(articleId);
        healthCircle.setRegisterid(registerId);
        healthCircle.setContent(content);
        healthCircle.setPraisenum(0);
        healthCircle.setSendtime(new Date());
        healthCircle.setCreateDate(new Date());
        healthCircle.setCreateBy(registerId);
        healthCircle.setUpdateDate(new Date());
        healthCircle.setUpdateBy(registerId);
        healthCircleRepository.save(healthCircle);
        return healthCircle;
    }

    @Override
    public List<CommunityDiscuss> getArticleDiscuss(String articleId,String order,Date flag) {
        Pageable pageable = PageFactory.create(1, 15, order);
        if(flag==null){
            return communityDiscussRepository.findByArticleId(articleId,pageable).getContent();
        }
        return communityDiscussRepository.findByArticleIdWithFlag(articleId, flag, pageable).getContent();
    }

    @Override
    public List<ArticleAttach> getArticleAttachs(String articleId) {
        return articleAttachRepository.findByArticleid(articleId);
    }

    @Override
    public void forward(String registerId, String title, String desc, String thumb, String url,String content) {
        HealthCircle healthCircle = saveArticle(registerId, content);
        ArticleTransmit articleTransmit = saveArticleTransmit(title, desc, thumb, url);
        healthCircle.setTransmitid(articleTransmit.getId());
        healthCircleRepository.save(healthCircle);
    }

    @Override
    public ArticleTransmit saveArticleTransmit(String title, String desc, String thumb, String url){
        ArticleTransmit articleTransmit = new ArticleTransmit();
        articleTransmit.setId(IdGen.uuid());
        articleTransmit.setTitle(title);
        articleTransmit.setUrl(url);
        articleTransmit.setSubtitle(desc);
        articleTransmit.setPic(thumb);
        articleTransmit.setCreateDate(new Date());
        articleTransmit.setUpdateDate(new Date());
        articleTransmit.setDelFlag("0");
        return articleTransmitRepository.save(articleTransmit);
    }

    @Override
    public ArticleTransmit getArticleForward(String articleId) {
        HealthCircle circle = healthCircleRepository.findOne(articleId);
        if(null!=circle){
            String transmitid = circle.getTransmitid();
            if(StringUtils.isNotEmpty(transmitid)){
                return articleTransmitRepository.findOne(transmitid);
            }
        }
        return null;
    }

    @Override
    public String getArticleAttachType(String articleId){
        List<ArticleAttach> articleAttachs = getArticleAttachs(articleId);
        for(ArticleAttach articleAttach:articleAttachs){
            if(articleAttach.getAttachtype()==1){
                return "1";
            }else if(articleAttach.getAttachtype()==2){
                return "2";
            }
        }
        if(null!=getArticleForward(articleId)){
            return "3";
        }
        return "0";
    }

    @Override
    public Boolean report(String uid, String reportId, Integer contentType, Integer reportType) {
        Assert.isTrue(contentType==1||contentType==2,"接收参数值："+contentType+"，举报类型错误(1.帖子 2.评论)");
        Assert.isTrue(reportType>=1&&reportType<=5,"接收参数值："+reportType+"，举报理由错误(1.色情 2.政治 3.骂人 4.广告 5.其他)");
        Boolean success = CircleReportUtils.reportOne(reportId, uid, String.valueOf(contentType));
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
                HealthCircle healthCircle = healthCircleRepository.findOne(reportId);
                reportContent = null != healthCircle ? healthCircle.getContent() : "";
                reportContentUserId = healthCircle.getRegisterid();
            }else if (contentType == 2){
                //评论
                CommunityDiscuss communityDiscuss = communityDiscussRepository.findOne(reportId);
                reportContent = null != communityDiscuss ? communityDiscuss.getContent() : "";
                reportContentUserId = communityDiscuss.getRegisterid();
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
            circleReportRepository.save(circleReport);
        }else{
            report.setReportnum(report.getReportnum()+1);
            report.setUpdateDate(new Date());
            circleReportRepository.save(report);
        }
    }

    @Override
    public Boolean delArticle(String uid, String articleId) {
        HealthCircle article = healthCircleRepository.findUserAndCircle(uid, articleId);
        if(article!=null){
            article.setDelFlag("1");
            healthCircleRepository.save(article);
            return true;
        }
        return false;
    }

    @Override
    public Boolean delComment(String uid, String commentId) {
        CommunityDiscuss comment = communityDiscussRepository.findUserAndCommentId(uid, commentId);
        if(comment!=null){
            comment.setDelFlag("1");
            communityDiscussRepository.save(comment);
            return true;
        }
        return false;
    }
}
