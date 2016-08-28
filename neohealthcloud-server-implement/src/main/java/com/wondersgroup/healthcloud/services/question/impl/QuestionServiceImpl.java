package com.wondersgroup.healthcloud.services.question.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import com.wondersgroup.healthcloud.jpa.entity.question.Reply;
import com.wondersgroup.healthcloud.jpa.entity.question.ReplyGroup;
import com.wondersgroup.healthcloud.jpa.repository.question.QuestionRepository;
import com.wondersgroup.healthcloud.jpa.repository.question.ReplyGroupRepository;
import com.wondersgroup.healthcloud.jpa.repository.question.ReplyRepository;
import com.wondersgroup.healthcloud.services.question.QuestionService;
import com.wondersgroup.healthcloud.services.question.dto.QuestionComment;
import com.wondersgroup.healthcloud.services.question.dto.QuestionDetail;
import com.wondersgroup.healthcloud.services.question.dto.QuestionGroup;
import com.wondersgroup.healthcloud.services.question.dto.QuestionInfoForm;
import com.wondersgroup.healthcloud.services.question.exception.ErrorReplyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("questionService")
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository repository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ReplyGroupRepository replyGroupRepository;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jt;

    @Override
    public String saveQuestion(Question question) {
        String id= IdGen.uuid();
        question.setId(id);
        question.setStatus(1);
        question.setCreateTime(new Date());
        question.setUpDate(new Date());
        question.setComment_count(0);
        question.setNewest_answer_id("");
        question.setIsValid(1);
        repository.saveAndFlush(question);
        return id;
    }

    @Override
    public List<QuestionInfoForm> queryQuerstionList(String userId, int pageNo) {
        String sql="SELECT t1.id, t1.status, t1.content, t2.name,date_format(t1.create_time,'%Y-%c-%d %H:%i') date,"
                + "t1.comment_count FROM app_tb_neoquestion t1 LEFT JOIN doctor_account_tb t2 ON t1.newest_answer_id=t2.id"
                + " WHERE t1.asker_id='"+userId+"' ORDER BY t1.has_noread_comment desc, t1.status asc, t1.create_time desc"
                + " LIMIT "+pageNo*10+","+10;
        List<Map<String, Object>> list=getJt().queryForList(sql);
        if(null != list){
            return transformat(list);
        }
        return null;
    }

    @Override
    public QuestionDetail queryQuestionDetail(String questionId) {
        Question qt=repository.findOne(questionId);
        if (null == questionId){
            throw new ErrorReplyException("问题无效！");
        }
        if (qt.getHasReply() == 1){
            qt.setNewest_answer_id("");
            qt.setHasReply(0);
            repository.saveAndFlush(qt);
        }
        QuestionDetail detail = new QuestionDetail(qt);
        List<QuestionGroup> group = getQuestionGroup(questionId, true);
        if (qt.getStatus() == 3 && null != group && !group.isEmpty()){
            for (QuestionGroup questionGroup : group){
                questionGroup.setIsReply(false);
            }
        }
        detail.setGroup(group);
        return detail;
    }
    @Override
    public List<QuestionGroup> getQuestionGroup(String questionId, Boolean from_user) {
        String sql="SELECT t1.id, t1.answer_id as doctorId, t2.name, t2.avatar, t4.duty_name "
                + " FROM app_tb_neogroup t1 "
                + " LEFT JOIN doctor_account_tb t2 ON t1.answer_id=t2.id "
                + " LEFT JOIN doctor_info_tb t3 ON t2.id=t3.id "
                + " LEFT JOIN t_dic_duty t4 ON t3.duty_id=t4.duty_id "
                + " WHERE t1.question_id='"+questionId+"' order by t1.new_comment_time DESC";
        List<Map<String, Object>> list=getJt().queryForList(sql);
        List<QuestionGroup> groups=new ArrayList<>();
        for(Map<String, Object> map :list){
            QuestionGroup group=new QuestionGroup(map);
            String groupId=(String) map.get("id");
            List<QuestionComment> comments=getQuestionComment(groupId);
            int size=comments.size();
            if(size>0){
                group.setQuestionComment(comments);
                Boolean userCanReply = comments.get(size-1).getUserReply() == 0;
                userCanReply = from_user ? userCanReply : !userCanReply;
                group.setIsReply(userCanReply);
            }
            groups.add(group);
        }
        return groups;
    }

    @Override
    public List<QuestionComment> getQuestionComment(String groupId) {
        String sql="SELECT comment_group_id,content,content_imgs,is_user_reply,date_format(create_time,'%m-%d %H:%i') date"
                + " FROM app_tb_neoreply WHERE comment_group_id='"+groupId+"' ORDER BY create_time";
        List<Map<String, Object>> list=getJt().queryForList(sql);
        List<QuestionComment> comments=new ArrayList<>();
        for(Map<String, Object> map:list){
            QuestionComment comment=new QuestionComment(map);
            comments.add(comment);
        }
        return comments;
    }
    @Override
    public void saveReplay(Reply reply) {
        reply.setCreateTime(new Date());
        reply.setUserReply(1);
        reply.setIsValid(1);
        replyRepository.saveAndFlush(reply);
        ReplyGroup group=replyGroupRepository.findOne(reply.getGroupId());
        group.setNewCommentTime(new Date());
        group.setHasNewUserComment(1);
        group.setStatus(1);
        replyGroupRepository.saveAndFlush(group);
    }

    @Override
    public Boolean queryHasNewReply(String userId) {
        List <Question> list=repository.findByaskId(userId);
        Boolean has=false;
        if(list.size()>0){
            for(Question qt:list){
                int isReply=qt.getHasReply();
                if(isReply==1){
                    has=true;
                    break;
                }
            }
        }
        return has;
    }

    @Override
    public ReplyGroup queryAnswerId(String groupId) {
        ReplyGroup group=replyGroupRepository.findOne(groupId);
        return group;
    }

    private List<QuestionInfoForm> transformat(List<Map<String, Object>> param){
        List <QuestionInfoForm> list=new ArrayList<>();
        for(Map<String, Object> map:param){
            QuestionInfoForm qf=new QuestionInfoForm(map);
            list.add(qf);
        }
        return list;
    }
     /**
     * 获取jdbc template
     *
     * @return
     */
    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }

}
