package com.wondersgroup.healthcloud.services.question;


import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import com.wondersgroup.healthcloud.services.question.dto.DoctorQuestionDetail;
import com.wondersgroup.healthcloud.services.question.dto.DoctorQuestionMsg;
import com.wondersgroup.healthcloud.services.question.dto.QuestionInfoForm;

import java.util.List;

public interface DoctorQuestionService {

    /**
     * 查询问诊详情
     * @param questionId
     * @return
     */
    DoctorQuestionDetail queryQuestionDetail(String doctorId, String questionId);

    /**
     * 获得问答广场，进行中的问答
     * 返回数据为pageSize+1(用以判断是否有更多)
     * 使用方需要截取长度
     */
    List<QuestionInfoForm> getQuestionSquareList(String doctorId,int page, int pageSize);

    /**
     * 查询医生的问诊列表
     * 返回数据为pageSize+1(用以判断是否有更多)
     * 使用方需要截取长度
     */
    List<QuestionInfoForm> getDoctorPrivateQuestionLivingList(String doctorId, int page, int pageSize);

    /**
     * 获取医生回复的问题列表
     * 返回数据为pageSize+1(用以判断是否有更多)
     * 使用方需要截取长度
     */
    List<QuestionInfoForm> getDoctorReplyQuestionList(String doctorId, int page, int pageSize);


    /**
     * 查询医生没有读过的问诊列表
     */
    List<DoctorQuestionMsg> getDoctorNoReadQuestionList(String doctorId, int page, int pageSize);

    /**
     * 查询医生没有读过的问诊新回复
     */
    List<DoctorQuestionMsg> getDoctorNoReadCommentList(String doctorId, int page, int pageSize);

    /**
     * 查询是否有未读提问
     */
    Boolean hasNewQuestionForDoctor(String doctorId);

    /**
     * 查询是否有未读回复
     */
    Boolean hasNewCommentForDoctor(String doctorId);

    /**
     * 添加回复
     */
    void doctorReplay(String doctorId, String question_id, String reply_content, String reply_content_imgs);

    /**
     * 根据Id查询问题
     * @param id
     * @return
     */
    Question queryQuestion(String id);

    /**
     * 查询未查看追问
     * @param
     * @return
     */
    public int queryUnreadCount(String doctorId);


    /**
     * 查询医生已回答的问题数量
     * @param uid
     * @return
     */
    int queryAnsweredCount(String uid);
}
