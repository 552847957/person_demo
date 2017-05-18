package com.wondersgroup.healthcloud.services.question.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import com.wondersgroup.healthcloud.jpa.entity.question.Reply;
import com.wondersgroup.healthcloud.jpa.entity.question.ReplyGroup;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.question.QuestionRepository;
import com.wondersgroup.healthcloud.jpa.repository.question.ReplyGroupRepository;
import com.wondersgroup.healthcloud.jpa.repository.question.ReplyRepository;
import com.wondersgroup.healthcloud.services.question.DoctorQuestionService;
import com.wondersgroup.healthcloud.services.question.dto.*;
import com.wondersgroup.healthcloud.services.question.exception.ErrorReplyException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

@Service("doctorQuestionService")
public class DoctorQuestionServiceImpl implements DoctorQuestionService {

    @Autowired
    private QuestionServiceImpl questionService;

    @Autowired
    private QuestionRepository repository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ReplyGroupRepository replyGroupRepository;

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;


    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jt;

    @Override
    public DoctorQuestionDetail queryQuestionDetail(String doctorId, String questionId) {
        Question question = repository.findOne(questionId);
        if (null == question) {
            throw new ErrorReplyException("问题无效！");
        }
        if (question.getIs_new_question() == 1) {
            question.setIs_new_question(0);
            repository.saveAndFlush(question);
        }
        ReplyGroup myGroupInfo = replyGroupRepository.getCommentGroup(questionId, doctorId);
        if (null != myGroupInfo && myGroupInfo.getHasNewUserComment() == 1) {
            myGroupInfo.setHasNewUserComment(0);
            replyGroupRepository.saveAndFlush(myGroupInfo);
        }
        DoctorQuestionDetail questionDetail = new DoctorQuestionDetail(question);

        //获取问题组
        QuestionGroup group = questionService.getQuestionGroup(questionId, doctorId);
        if (null != group) {
            questionDetail.setStatus(group.getStatus());
            questionDetail.setGroup(group);
        } else {
            questionDetail.setStatus(1);
        }

        return questionDetail;
    }


    @Override
    public List<QuestionInfoForm> getQuestionSquareList(String doctor_id, int page, int pageSize) {
        List<Object> elementType = new ArrayList<>();
        String sql = "SELECT t1.id,t1.content,t1.is_new_question as isNoRead,t1.assign_answer_id,date_format(t1.create_time,'%Y-%m-%d %H:%i') as date " +
                " FROM app_tb_neoquestion t1 LEFT JOIN app_tb_neogroup t2 ON t1.id=t2.question_id " +
                "WHERE (t1.assign_answer_id='' OR t1.assign_answer_id=?) AND t1.status<>3 AND t1.id not in(select t1.id FROM app_tb_neoquestion t1 " +
                "INNER JOIN app_tb_neogroup t2 ON t1.id=t2.question_id WHERE answer_id=?) " +
                "GROUP BY id ORDER BY assign_answer_id DESC,date DESC limit ?,?";
        elementType.add(doctor_id);
        elementType.add(doctor_id);
        elementType.add((page - 1) * pageSize);
        elementType.add(pageSize + 1);
        List<Map<String, Object>> list = getJt().queryForList(sql, elementType.toArray());
        if (null != list) {
            return transformat(list);
        }
        return null;
    }

    @Override
    public List<QuestionInfoForm> getDoctorPrivateQuestionLivingList(String doctor_id, int page, int pageSize) {
        List<Object> elementType = new ArrayList<>();
        List<Object> elementType2 = new ArrayList<>();
        String sql = "SELECT t1.id,t1.content,date_format(t1.create_time,'%Y-%m-%d %H:%i') as date," +
                "t2.has_new_user_comment as isNoRead,t2.status ,date_format(t2.new_comment_time,'%Y-%m-%d %H:%i') as date2 " +
                "FROM app_tb_neoquestion t1 LEFT JOIN app_tb_neogroup t2 ON t1.id=t2.question_id " +
                "WHERE answer_id=? AND t1.status<>3 " +
                "ORDER BY status,date2 DESC limit ?,?";
        String sql2 = "SELECT t1.id,t1.content,date_format(t1.create_time,'%Y-%m-%d %H:%i') as date," +
                "t2.has_new_user_comment as isNoRead,t2.status ,date_format(t2.new_comment_time,'%Y-%m-%d %H:%i') as date2 " +
                "FROM app_tb_neoquestion t1 LEFT JOIN app_tb_neogroup t2 ON t1.id=t2.question_id " +
                "WHERE answer_id=? " +
                "ORDER BY status,date DESC limit ?,?";
        elementType.add(doctor_id);
        elementType.add((page - 1) * pageSize);
        elementType.add(pageSize + 1);
        List<Map<String, Object>> list = getJt().queryForList(sql, elementType.toArray());
        int size = list.size();
        elementType2.add(doctor_id);
        elementType2.add((page - 1) * pageSize + size);
        elementType2.add(pageSize + 1);
        List<Map<String, Object>> list2 = getJt().queryForList(sql2, elementType2.toArray());
        if (size < 11) {
            list.addAll(list2);
            return transformat(list);
        } else {
            list.addAll(list);
            return transformat(list);
        }
    }

    @Override
    public List<QuestionInfoForm> getDoctorReplyQuestionList(String doctorId, int page, int pageSize) {
        List<Object> elementType = new ArrayList<>();
        String sql = "SELECT q.id,q.status,q.content,date_format(q.create_time,'%Y-%m-%d %H:%i') as date,cg.has_new_user_comment as isNoRead,"
                + "q.comment_count FROM app_tb_neoquestion q LEFT JOIN app_tb_neogroup cg ON q.id=cg.question_id "
                + " WHERE cg.answer_id=? AND q.status>1 and q.is_valid=1 ORDER BY cg.has_new_user_comment DESC, q.status asc, q.create_time DESC limit ?,?";
        elementType.add(doctorId);
        elementType.add((page - 1) * pageSize);
        elementType.add(pageSize + 1);
        List<Map<String, Object>> list = getJt().queryForList(sql, elementType.toArray());
        if (null != list) {
            return transformat(list);
        }
        return null;
    }

    @Override
    public List<DoctorQuestionMsg> getDoctorNoReadQuestionList(String doctorId, int page, int pageSize) {
        List<Object> elementType = new ArrayList<>();
        String sql = "SELECT q.id,q.content,date_format(q.create_time,'%Y-%m-%d %H:%i') as date "
                + " FROM app_tb_neoquestion q "
                + " WHERE q.assign_answer_id=? and q.status=1 and q.is_valid=1 and q.is_new_question=1 ORDER BY q.create_time DESC limit ?,?";
        elementType.add(doctorId);
        elementType.add((page - 1) * pageSize);
        elementType.add(pageSize + 1);
        List<Map<String, Object>> list = getJt().queryForList(sql, elementType.toArray());
        List<DoctorQuestionMsg> rt = new ArrayList<>();
        if (null != list && !list.isEmpty()) {
            for (Map<String, Object> map : list) {
                DoctorQuestionMsg qf = new DoctorQuestionMsg(map);
                qf.setType(1);
                rt.add(qf);
            }
        }
        return rt;
    }

    @Override
    public List<DoctorQuestionMsg> getDoctorNoReadCommentList(String doctorId, int page, int pageSize) {
        List<Object> elementType = new ArrayList<>();
        String sql = "SELECT question_id as id, content, date_format(create_time,'%Y-%m-%d %H:%i') as date "
                + " FROM " +
                "(  select g.`id`, g.question_id, c.id AS comment_id, c.content, c.create_time " +
                " from app_tb_neogroup g left join comment_tb c on c.comment_group_id=g.id "
                + " WHERE g.answer_id=? and g.is_valid=1 and g.has_new_user_comment=1 order by c.create_time DESC ) g"
                + " group by g.id ORDER BY g.create_time DESC limit ?,?";
        elementType.add(doctorId);
        elementType.add((page - 1) * pageSize);
        elementType.add(pageSize + 1);
        List<Map<String, Object>> list = getJt().queryForList(sql, elementType.toArray());

        List<DoctorQuestionMsg> rt = new ArrayList<>();
        if (null != list && !list.isEmpty()) {
            for (Map<String, Object> map : list) {
                DoctorQuestionMsg qf = new DoctorQuestionMsg(map);
                qf.setType(2);
                rt.add(qf);
            }
        }
        return rt;
    }

    @Override
    public Boolean hasNewQuestionForDoctor(String doctorId) {

        String sqlQuestion = "SELECT t1.id FROM app_tb_neoquestion t1 LEFT JOIN app_tb_neogroup t2 ON t1.id=t2.question_id " +
                "WHERE t1.assign_answer_id=? and t1.status<>3 AND t2.status IS NULL and t1.is_new_question=1 and t1.is_valid=1 limit 1";
        List<Map<String, Object>> noReadQ = getJt().queryForList(sqlQuestion, new Object[]{doctorId});

        return null != noReadQ && !noReadQ.isEmpty();
    }

    @Override
    public Boolean hasNewCommentForDoctor(String doctorId) {
        String sqlCommon = "SELECT t1.id FROM app_tb_neoquestion t1 LEFT JOIN app_tb_neogroup t2 ON t1.id=t2.question_id " +
                "WHERE t2.answer_id=? and t2.status=1 and t2.has_new_user_comment=1 and t1.is_valid=1 limit 1";
        List<Map<String, Object>> noReadC = getJt().queryForList(sqlCommon, new Object[]{doctorId});
        return null != noReadC && !noReadC.isEmpty();
    }

    @Override
    public void doctorReplay(String doctorId, String question_id, String reply_content, String reply_content_imgs) {
        Reply lastReply = replyRepository.getCommonGroupLastReply(question_id, doctorId);
        if (null != lastReply && lastReply.getUserReply() == 0) {
            //最后一次是医生回复的，不能再次回复
            throw new ErrorReplyException("请先等待用户回复哦！");
        }
        Question question = repository.findOne(question_id);
        if (null == question || question.getIsValid() == 0) {
            throw new ErrorReplyException("问题无效！不能回复了");
        }
        if (question.getStatus() == 3) {
            throw new ErrorReplyException("问题已经被关闭，不能进行回复了！");
        }
        if (StringUtils.isNotEmpty(question.getAnswerId()) && !question.getAnswerId().equals(doctorId)) {
            throw new ErrorReplyException("您不是他的家庭医生，暂时还不能回复哦！");
        }
        int comment_count = null == question.getComment_count() ? 0 : question.getComment_count();
        Date nowDate = new Date();
        ReplyGroup replyGroup = new ReplyGroup();
        if (null == lastReply) {
            //医生第一次回复
            comment_count++;
            replyGroup.setCreateTime(nowDate);
            replyGroup.setAnswer_id(doctorId);
            replyGroup.setId(IdGen.uuid());
            replyGroup.setNewCommentTime(nowDate);
            replyGroup.setQuestion_id(question_id);
            replyGroup.setIs_valid(1);
            replyGroup.setStatus(2);
            replyGroup = replyGroupRepository.saveAndFlush(replyGroup);
        } else {
            replyGroup = replyGroupRepository.findOne(lastReply.getGroupId());
            replyGroup.setNewCommentTime(nowDate);
            replyGroup.setHasNewUserComment(0);
            replyGroup.setStatus(2);
            replyGroupRepository.saveAndFlush(replyGroup);
        }

        //记录回复信息
        Reply newReply = new Reply();
        newReply.setId(IdGen.uuid());
        newReply.setContent(reply_content);
        newReply.setContentImgs(reply_content_imgs);
        newReply.setGroupId(replyGroup.getId());
        newReply.setCreateTime(nowDate);
        newReply.setUserReply(0);
        newReply.setIsValid(1);
        replyRepository.saveAndFlush(newReply);

        question.setStatus(2);
        question.setHasReply(1);
        question.setIs_new_question(0);
        question.setNewest_answer_id(doctorId);
        question.setComment_count(comment_count);
        repository.saveAndFlush(question);

    }

    @Override
    public Question queryQuestion(String id) {
        Question qt = repository.findOne(id);
        return qt;
    }

    @Override
    public int queryUnreadCount(String doctorId) {

        int unreadQuestion = repository.unreadQuestionuCount(doctorId);
        int unreadAsk = repository.unreadAskCount(doctorId);
        return unreadQuestion + unreadAsk;
    }

    @Override
    public AllQuestionDetails queryAllQuestionDetails(String doctorId, String questionId) {
        String questionSql = "select a.id,a.sex,a.age,a.content,a.content_imgs,a.create_time from app_tb_neoquestion as a where id = '" + questionId + "' ";
        AllQuestionDetails allQuestionDetails = null;
        Map<String, Object> map = jt.queryForMap(questionSql);
        if (null != map && map.entrySet().size() > 0) {
            //组 select * from app_tb_neogroup as c where c.question_id = '10309dc177894a4782d9444ff3ad0b9b' ORDER BY create_time desc;

            String id = String.valueOf(map.get("id"));
            String sex = String.valueOf(map.get("sex"));
            int age = Integer.parseInt(String.valueOf(map.get("age")));
            String content = String.valueOf(map.get("content"));
            String contentImgs = String.valueOf(map.get("content_imgs"));
            String date = String.valueOf(map.get("create_time"));
            allQuestionDetails = new AllQuestionDetails(id, sex, age, content, contentImgs, date);


            List<ReplyGroup> groupList = replyGroupRepository.getCommentGroupList(questionId);

            List<String> doctorIds = new ArrayList<>();
            List<String> groupIds = new ArrayList<>();
            for (ReplyGroup gp : groupList) {
                groupIds.add(gp.getId());
                doctorIds.add(gp.getAnswer_id());
            }

            List<DoctorAccount> doctorList = doctorAccountRepository.findDoctorsByIds(doctorIds);
            Map<String, DoctorAccount> doctorMap = doctorList2Map(doctorList);
            Map<String, ReplyGroup> groupMap = groupList2Map(groupList);


            for (ReplyGroup rg : groupList) {//一个组放到一个集合里
                List list = new ArrayList();
                List<Reply> replyList = replyRepository.getReplyByGroupId(rg.getId());
                for (Reply rp : replyList) {
                    if (rp.getUserReply() == 0) {//0:医生的回复,1:用户的回复
                        ReplyGroup group = groupMap.get(rp.getGroupId());
                        DoctorAccount replayDoctor = doctorMap.get(group.getAnswer_id());
                        int questionType = doctorId.equals(replayDoctor.getId()) ? 0 : 2;  //0 我的回复，1 患者追问  2 其他医生回复
                        list.add(new DoctorAnster(questionType, replayDoctor.getAvatar(), replayDoctor.getId(), replayDoctor.getName(), rp.getContent(), rp.getCreateTime().toString()));
                    } else if (rp.getUserReply() == 1) {
                        list.add(new PationAsk(1, sex, age, rp.getContent(), rp.getContentImgs(), date));
                    }
                }
                allQuestionDetails.getDialogs().add(list);
            }
        }


       /*  if (null != map && map.entrySet().size() > 0) {
            String id = String.valueOf(map.get("id"));
            String sex = String.valueOf(map.get("sex"));
            int age = Integer.parseInt(String.valueOf(map.get("age")));
            String content = String.valueOf(map.get("content"));
            String contentImgs = String.valueOf(map.get("content_imgs"));
            String date = String.valueOf(map.get("create_time"));
            allQuestionDetails = new AllQuestionDetails(id, sex, age, content, contentImgs, date);
            List<ReplyGroup> groupList = replyGroupRepository.getCommentGroupList(questionId);
            if (CollectionUtils.isNotEmpty(groupList)) {
                List<String> doctorIds = new ArrayList<>();
                List<String> groupIds = new ArrayList<>();
                for (ReplyGroup gp : groupList) {
                    groupIds.add(gp.getId());
                    doctorIds.add(gp.getAnswer_id());
                }

                List<DoctorAccount> doctorList = doctorAccountRepository.findDoctorsByIds(doctorIds);
                List<Reply> replyList = replyRepository.getCommentGroupList(groupIds);
                if (CollectionUtils.isNotEmpty(groupList) && CollectionUtils.isNotEmpty(doctorList)) {
                    Map<String, DoctorAccount> doctorMap = doctorList2Map(doctorList);
                    Map<String, ReplyGroup> groupMap = groupList2Map(groupList);

                    List list = new ArrayList();
                    for (Reply rp : replyList) {
                        // 查找顺序 groupId ---> answerId ---> doctorId---> doctorAccount
                        if (rp.getUserReply() == 0) {//0:医生的回复,1:用户的回复
                            ReplyGroup group = groupMap.get(rp.getGroupId());
                            DoctorAccount replayDoctor = doctorMap.get(group.getAnswer_id());
                            int questionType = doctorId.equals(replayDoctor.getId()) ? 0 : 2;  //0 我的回复，1 患者追问  2 其他医生回复
                            list.add(new DoctorAnster(questionType,replayDoctor.getAvatar(),replayDoctor.getId(),replayDoctor.getName(),rp.getContent(),rp.getCreateTime().toString()));
                        } else if (rp.getUserReply() == 1) {
                            list.add(new PationAsk(1,sex,age,rp.getContent(),rp.getContentImgs(),date));
                        }
                    }
                    allQuestionDetails.setDialogs(list);
                }
            }

        }*/

        return allQuestionDetails;
    }

    private Map<String, DoctorAccount> doctorList2Map(List<DoctorAccount> list) {
        Map<String, DoctorAccount> map = new HashMap<>();
        for (DoctorAccount doctor : list) {
            map.put(doctor.getId(), doctor);
        }
        return map;
    }

    private Map<String, ReplyGroup> groupList2Map(List<ReplyGroup> list) {
        Map<String, ReplyGroup> map = new HashMap<>();
        for (ReplyGroup group : list) {
            map.put(group.getId(), group);
        }
        return map;
    }

    private List<QuestionInfoForm> transformat(List<Map<String, Object>> param) {
        List<QuestionInfoForm> list = new ArrayList<>();
        for (Map<String, Object> map : param) {
            QuestionInfoForm qf = new QuestionInfoForm(map);
            list.add(qf);
        }
        return list;
    }

    private List<QuestionInfoForm> transformat2(List<Map<String, Object>> param) {
        List<QuestionInfoForm> list = new ArrayList<>();
        for (Map<String, Object> map : param) {
            if (map.get("status") != null && (int) map.get("status") == 3) {
                break;
            }
            QuestionInfoForm qf = new QuestionInfoForm(map);
            list.add(qf);
        }
        //已關閉的問題
        List<Map<String, Object>> maps = param.subList(list.size(), param.size());

        List<QuestionInfoForm> closeQuestions = transformat(maps);
        Collections.sort(closeQuestions, new Comparator<QuestionInfoForm>() {
            public int compare(QuestionInfoForm info1, QuestionInfoForm info2) {
                //按照提問時間进行降序排列
                int res = info1.getDate().compareTo(info2.getDate());
                if (res < 1) {
                    return 1;
                }
                if (res == 0) {
                    return 0;
                }
                return -1;
            }
        });
        list.addAll(closeQuestions);
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
