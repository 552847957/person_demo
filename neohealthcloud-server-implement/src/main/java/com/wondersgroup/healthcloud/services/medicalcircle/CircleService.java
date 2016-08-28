package com.wondersgroup.healthcloud.services.medicalcircle;

import java.util.Date;
import java.util.List;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleAttach;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleTransmit;
import com.wondersgroup.healthcloud.jpa.entity.circle.CommunityDiscuss;
import com.wondersgroup.healthcloud.jpa.entity.circle.HealthCircle;

/**
 * 健康圈
 */
public interface CircleService {

    /**
     * 获得最热健康圈列表
     * @return
     */
    List<HealthCircle> getHotArticles(String flag,Integer num,Float praisenum);

    /**
     * 获得健康圈列表
     * @return
     */
    List<HealthCircle> getAllArticles(String order,Date flag);

    /**
     * 获得单个健康圈内容
     * @param articleId
     * @return
     */
    HealthCircle getArticle(String articleId);

    /**
     * 获得个人健康圈列表
     * @param registerId
     * @return
     */
    List<HealthCircle> getUserArticles(String registerId,Date flag,String order);

    /**
     * 获得文章评论数
     * @param ArticleId
     * @return
     */
    Long getCommentsNum(String ArticleId);

    /**
     * 评论
     * @param registerId
     * @param articleId
     * @param content
     */
    void comment(String registerId,String articleId,String content);

    /**
     * 点赞
     * @param registerId
     * @param articleId
     */
    Boolean like(String registerId,String articleId);

    /**
     * 取消点赞
     * @param registerId
     * @param articleId
     */
    Boolean unlike(String registerId,String articleId);

    /**
     * 发布内容
     * @param registerId
     * @param content
     */
    void publish(String registerId,String content);

    /**
     * 发布内容（包含文件）
     * @param registerId
     * @param content
     * @param fileIds
     */
    void publishWithFiles(String registerId,String content,List<String> fileIds,Integer fileType);

    /**
     * 获取文章评论列表
     * @param articleId
     * @return
     */
    List<CommunityDiscuss> getArticleDiscuss(String articleId,String order,Date flag);

    /**
     * 获取文章附件列表
     * @param articleId
     * @return
     */
    List<ArticleAttach> getArticleAttachs(String articleId);

    /**
     * 转发
     * @param registerId
     */
    void forward(String registerId,String title,String desc,String thumb,String url,String content);


    ArticleTransmit saveArticleTransmit(String title, String desc, String thumb, String url);

    /**
     * 获取文章转发内容
     * @param articleId
     * @return
     */
    ArticleTransmit getArticleForward(String articleId);

    /**
     * 获取文章附件类型（图片，音频，转发）
     * @param articleId
     * @return
     */
    String getArticleAttachType(String articleId);


    /**
     * 举报
     * @param uid 用户
     * @param reportId 举报内容id
     * @param contentType 举报类型(1：帖子 2：评论)
     * @param reportType 举报理由(1：色情 2：政治 3：骂人 4：广告 5:其他)
     * @return
     */
    Boolean report(String uid,String reportId,Integer contentType,Integer reportType);

    /**
     * 删除帖子
     * @param uid
     * @param articleId
     * @return
     */
    Boolean delArticle(String uid,String articleId);

    /**
     * 删除评论
     * @param uid
     * @param commentId
     * @return
     */
    Boolean delComment(String uid,String commentId);
}
