package com.wondersgroup.healthcloud.services.question;


import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import com.wondersgroup.healthcloud.jpa.entity.question.Reply;
import com.wondersgroup.healthcloud.jpa.entity.question.ReplyGroup;
import com.wondersgroup.healthcloud.services.question.dto.QuestionComment;
import com.wondersgroup.healthcloud.services.question.dto.QuestionDetail;
import com.wondersgroup.healthcloud.services.question.dto.QuestionGroup;
import com.wondersgroup.healthcloud.services.question.dto.QuestionInfoForm;

import java.util.List;

public interface QuestionService {
    /**
     * 提交问诊
     * @param question
     */
    String saveQuestion(Question question);
    /**
     * 查询问诊
     * @param userId
     * @param pageNo
     * @return
     */
    List<QuestionInfoForm> queryQuerstionList(String userId, int pageNo);

    /**
     * 查询问诊详情
     * @param questionId
     * @return
     */
    QuestionDetail queryQuestionDetail(String questionId);
    /**
     * 添加回复
     * @param reply
     */
    void saveReplay(Reply reply);

    List<QuestionGroup> getQuestionGroup(String questionId, Boolean from_user);

    List<QuestionComment> getQuestionComment(String groupId);
    /**
     * 查询是否有未读回复
     * @param userId
     * @return
     */
    Boolean queryHasNewReply(String userId);
    /**
     * 查询回答者
     * @param
     * @return
     */
    ReplyGroup queryAnswerId(String groupId);
}
