package com.wondersgroup.healthcloud.services.medicalcircle;

import java.util.Date;

import java.util.List;
import java.util.Map;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleAttach;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleTransmit;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircle;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleAttention;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleCollect;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleCommunity;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleReply;

public interface MedicalCircleService {

    /**
     * 获得医学圈列表
     * @return
     */
    List<MedicalCircle> getAllMedicalCircle(Integer[] type,String order,Date flag);

    /**
     * 获得单个医学圈内容
     * @param circleId
     * @return
     */
    MedicalCircle getMedicalCircle(String circleId);

    /**
     * 获得个人健康圈列表
     * @param doctorId
     * @return
     */
    List<MedicalCircle> getUserMedicalCircle(String doctorId,Integer[] type,Date flag,String order);

    /**
     * 获得个人健康圈最新的一条有效数据
     * @param doctorId
     * @param type(1:帖子 2:病例 3:动态)
     * @return
     */
    MedicalCircle getUserMedicalCircleNewest(String doctorId, Integer[] type);

    /**
     * 获得文章评论数
     * @param circleId
     * @return
     */
    Long getCommentsNum(String circleId);

    /**
     * 评论
     * @param doctorId
     * @param circleId
     * @param content
     */
    MedicalCircleCommunity comment(String doctorId,String circleId,String content);

    /**
     * 回复
     * @param doctorId
     * @param replyId
     * @param content
     */
    MedicalCircleReply reply(String commentId,String doctorId, String replyId, String content);

    /**
     * 点赞
     * @param doctorId
     * @param circleId
     */
    Boolean like(String doctorId,String circleId);

    /**
     * 取消点赞
     * @param doctorId
     * @param circleId
     */
    Boolean unlike(String doctorId,String circleId);


    /**
     * 发布
     * @param doctorId
     * @param content
     * @param circleType
     * @param imageUrls
     */
    void publish(String doctorId,String title,String content,Integer circleType,List<String> imageUrls);

    /**
     * 获取文章评论列表
     * @param circleId
     * @return
     */
    List<MedicalCircleCommunity> getMedicalCircleComments(String circleId,String order,Date flag);

    /**
     * 获取圈子附件列表
     * @param circleId
     * @return
     */
    List<ArticleAttach> getCircleAttachs(String circleId);

    /**
     * 转发
     * @param registerId
     */
    void forward(String doctorId,String title,String desc,String thumb,String url,String content);


    /**
     * 获取文章转发内容
     * @param circleId
     * @return
     */
    ArticleTransmit getMedicalCircleForward(String circleId);


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
     * @param doctorId
     * @param circleId
     * @return
     */
    Boolean delMedicalCircle(String doctorId,String circleId);

    /**
     * 删除评论
     * @param doctorId
     * @param commentId
     * @return
     */
    Boolean delComment(String doctorId,String commentId);

    /**
     * 删除回复
     * @param replyId
     * @param replyDoctorId
     * @return
     */
    Boolean delReply(String replyId, String replyDoctorId);

    /**
     * 收藏
     * @param circleId
     * @param doctorId
     * @param type(1.医学圈 2.学苑)
     * @return
     */
    Boolean collect(String circleId, String doctorId,Integer type);

    /**
     * 取消收藏
     * @param circleId
     * @param doctorId
     * @param type(1.医学圈 2.学苑)
     * @return
     */
    Boolean delCollect(String circleId, String doctorId,Integer type);

    /**
     * 检查是否收藏
     * @param circleId
     * @param doctorId
     * @param type(1.医学圈 2.学苑)
     * @return
     */
    Boolean checkCollect(String circleId, String doctorId,Integer type);

    /**
     * 关注
     * @param attentionId
     * @param followedId
     * @return
     */
    Boolean attention(String attentionId, String followedId);

    /**
     * 取消关注
     * @param attentionId
     * @param followedId
     * @return
     */
    Boolean cancelAttention(String attentionId, String followedId);

    /**
     * 获取医生关注数
     * @param doctorId
     * @return
     */
    Long getDocFollowedNum(String doctorId);

    Long getDocFansNum(String doctorId);

    List<MedicalCircleAttention> getDocFollowedList(String doctorId,String order,Date flag);

    List<MedicalCircleAttention> getDocFansList(String doctorId, String order,Date flag);

    /**
     * 浏览
     * @param circleId
     * @param doctorId
     */
    void view(String circleId, String doctorId);

    /**
     * 文章浏览数
     * @param circleId
     */
    Long getCircleViews(String circleId);


    /**
     * 获取评论中的回复列表
     * @param commentId
     * @param flag
     * @param order
     * @return
     */
    List<MedicalCircleReply> getCommentReplyList(String commentId, Date flag, String order, Integer pageSize);

    String getFloor(Integer floor);

    Boolean isAttention(String attentionId, String followedId);

    Long getDynamicNum(String doctorId);

    Long getNoteCaseNum(String doctorId);

    List<Map<String,Integer>> getCollectCircleList(String doctorId, Date flag, String order);


    List<MedicalCircle> getCollectCircleList(String doctorId,Date flag,Integer[] type);

    List<MedicalCircleCollect> getCollectCircleListByType(String doctorId, int type, Date flag, String order);
}
