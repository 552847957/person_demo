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
import java.text.SimpleDateFormat;
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
        elementType.add(pageSize+1);
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
        String sql = "SELECT q.id,q.status,q.sex,q.content,date_format(q.create_time,'%Y-%m-%d %H:%i') as date,cg.has_new_user_comment as isNoRead,"
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

    /**
     * 查询医生已回答的数量
     * @param doctorId
     * @return
     */
    @Override
    public int queryAnsweredCount(String doctorId) {
        int answeredQuestion = repository.answeredQuestionuCount(doctorId);
        return answeredQuestion;
    }

    @Override
    public AllQuestionDetails queryAllQuestionDetails(String doctorId, String questionId) {
        String questionSql = "select a.id,a.sex,a.age,a.status,a.content,a.content_imgs,a.create_time from app_tb_neoquestion as a where id = '" + questionId + "' ";
        AllQuestionDetails allQuestionDetails = null;
        Map<String, Object> map = null;
        try {
            map = getJt().queryForMap(questionSql);
        }catch (Exception e){

        }

        Question question = repository.findOne(questionId);

        if (question.getIs_new_question() == 1) {
            question.setIs_new_question(0);
            repository.saveAndFlush(question);
        }

        ReplyGroup myGroupInfo = replyGroupRepository.getCommentGroup(questionId, doctorId);
        if (null != myGroupInfo && myGroupInfo.getHasNewUserComment() == 1) {
            myGroupInfo.setHasNewUserComment(0);
            replyGroupRepository.saveAndFlush(myGroupInfo);
        }

        if (null != map && map.entrySet().size() > 0) {

            String id = String.valueOf(map.get("id"));
            String sex = String.valueOf(map.get("sex"));
            int age = Integer.parseInt(String.valueOf(map.get("age")));
            Integer status = Integer.parseInt(String.valueOf(map.get("status"))) == 3 ? 3:0;// 0 当前医生没有回复
            String content = String.valueOf(map.get("content"));
            String contentImgs = String.valueOf(map.get("content_imgs")== null ? "": map.get("content_imgs"));
            String date = formatDate((Date)map.get("create_time"));
            allQuestionDetails = new AllQuestionDetails(id, sex, age, status,content, contentImgs, date);


            List<ReplyGroup> groupList = replyGroupRepository.getCommentGroupList(questionId);
            if(CollectionUtils.isEmpty(groupList)){
                return allQuestionDetails;
            }

            Reply lastReply = replyRepository.getCommonGroupLastReply(questionId,doctorId);

            if(null != lastReply && status != 3){
                if(lastReply.getUserReply() == 0){ // 0:医生的回复,1:用户的回复
                    allQuestionDetails.setStatus(1);
                }else if(lastReply.getUserReply() == 1){
                    allQuestionDetails.setStatus(2);
                }
            }else if(null == lastReply && status != 3){//问题未关闭，当前医生没有回复
                allQuestionDetails.setStatus(0);
            }


            List<String> doctorIds = new ArrayList<>();
            List<String> groupIds = new ArrayList<>();
            for (ReplyGroup gp : groupList) {
                groupIds.add(gp.getId());
                doctorIds.add(gp.getAnswer_id());
            }

            List<DoctorAccount> doctorList = doctorAccountRepository.findDoctorsByIds(doctorIds);

            if(CollectionUtils.isEmpty(doctorList)){
                return allQuestionDetails;
            }

            Map<String, DoctorAccount> doctorMap = doctorList2Map(doctorList);
            Map<String, ReplyGroup> groupMap = groupList2Map(groupList);

            List<Dialogs> dialogsGroupList = new ArrayList<Dialogs>();
            Dialogs currentDoctorDialogs = null; //当前医生对话


            int isCurrentDoctor = 0; // 1 是当前医生 0 不是
            for (ReplyGroup rg : groupList) {//一个组放到一个集合里
                Dialogs dialogs = new Dialogs();
                isCurrentDoctor = rg.getAnswer_id().equals(doctorId) ? 1:0 ;
                dialogs.setIsCurrentDoctor(isCurrentDoctor);
                List<DoctorAndPations> allDoctorList = new ArrayList<DoctorAndPations>();//所有的问答详情
                List<DoctorAndPations> currentDoctorList = new ArrayList<DoctorAndPations>(); //当前医生的问答详情
                List<Reply> replyList = replyRepository.getReplyByGroupId(rg.getId());
                if(isCurrentDoctor == 1){ //如果是当前医生，放到第一个
                    createDialogs(dialogs,replyList,currentDoctorList, doctorMap ,groupMap ,doctorId, sex,age);
                    currentDoctorDialogs = dialogs;
                }else{
                    createDialogs(dialogs,replyList,allDoctorList, doctorMap ,groupMap ,doctorId, sex,age);
                    dialogsGroupList.add(dialogs);
                }
            }

            DialogDoctorDateComparable sort = new DialogDoctorDateComparable();// true 按照 lastDoctorDate 降序排序
            sort.sortASC = false;
            Collections.sort(dialogsGroupList, sort);

            if(null != currentDoctorDialogs){ //把当前医生的对话放到第一
                dialogsGroupList.add(0,currentDoctorDialogs);
            }
            allQuestionDetails.setDialogs(dialogsGroupList);
        }

        return allQuestionDetails;
    }

    /**
     * 创建dialog对话对象
     * @param dialogs
     * @param replyList
     * @param list
     * @param doctorMap
     * @param groupMap
     * @param doctorId
     * @param sex
     * @param age
     */
    public void createDialogs(Dialogs dialogs,List<Reply> replyList,List<DoctorAndPations> list, Map<String, DoctorAccount> doctorMap ,Map<String, ReplyGroup> groupMap , String doctorId, String sex,int age){
        for (Reply rp : replyList) {
            if (rp.getUserReply() == 0) {//0:医生的回复,1:用户的回复
                ReplyGroup group = groupMap.get(rp.getGroupId());
                DoctorAccount replayDoctor = doctorMap.get(group.getAnswer_id());
                int questionType = doctorId.equals(replayDoctor.getId()) ? 0 : 2;  //0 我的回复，1 患者追问  2 其他医生回复
                String doctorName = doctorId.equals(replayDoctor.getId()) ? "我":replayDoctor.getName();
                list.add(new DoctorAnster(questionType, replayDoctor.getAvatar(), replayDoctor.getId(), doctorName, rp.getContent(), formatDate(rp.getCreateTime()),rp.getCreateTime()));
            } else if (rp.getUserReply() == 1) {
                list.add(new PationAsk(1, sex, age, rp.getContent(), rp.getContentImgs(), formatDate(rp.getCreateTime()),rp.getCreateTime()));
            }
        }
        //list 需要按时间排序
        DoctorAndPatientComparable dpSort = new DoctorAndPatientComparable();// true 按照 sortDate 升序排序
        dpSort.sortASC = true;
        Collections.sort(list, dpSort);

        dialogs.setMyDialogDetails(list);
    }

    @Override
    public List<QuestionInfoFormNew> getAllQuestionList(String doctorId, int page, int pageSize) {
        List<Object> elementType = new ArrayList<>();
        String sql = "select * from ( select  " +
                "(case a.assign_answer_id when  ? then 0 else 1 end ) as orderSort," +
                "                a.id,a.content, " +
                "                 (case (select count(1) from app_tb_neogroup as c where c.question_id = a.id ) " +
                "                 when 0 then 0  " +
                "                 else 2 end) as status," +
                "                 a.is_new_question as isNoRead," +
                "                 a.assign_answer_id, " +
                "                 date_format(a.create_time,'%Y-%m-%d %H:%i:%S') as date, " +
                "                 a.create_time as sortDate " +
                "                 from app_tb_neoquestion as a where not exists ( " +
                "                 select * from app_tb_neogroup as b where b.question_id = a.id " +
                "                 and b.answer_id = ? " +
                "                 )  and a.`status` <> 3  " +
                "                 and ( a.assign_answer_id = '' " +
                "                 or a.assign_answer_id= ? ) " +
                "                 ) as d order by d.orderSort ASC,d.`status` ASC,d.sortDate DESC limit ?,? " ;

        elementType.add(doctorId);
        elementType.add(doctorId);
        elementType.add(doctorId);
        elementType.add((page - 1) * pageSize);
        elementType.add(pageSize);
        List<Map<String, Object>> list = getJt().queryForList(sql, elementType.toArray());
        if (null != list) {
            List<QuestionInfoFormNew> questionInfoFormList = transformatNew(list);
            isAtCurrentDoctor(questionInfoFormList,doctorId);
            return questionInfoFormList;
        }
        return null;
    }

    @Override
    public List<QuestionInfoFormNew> getAllReplyQuestionList(String doctorId, int page, int pageSize) {
        List<Object> elementType = new ArrayList<>();
        String sql = "select " +
                " a.id, " +
                " a.content," +
                " case a.`status` when 3 then 3 else " +
                " (" +
                " select case d.is_user_reply when 0  then 1 else 2 end from  app_tb_neoreply as d INNER JOIN app_tb_neogroup as c on d.comment_group_id = c.id where c.question_id = a.id and c.answer_id = ?  order by d.create_time desc limit 0,1 " +
                " ) end as status," +
                " (select c.has_new_user_comment from app_tb_neogroup as c where c.question_id = a.id and c.answer_id = ? ) as isNoRead," +
                " a.assign_answer_id, " +
                " date_format(a.create_time,'%Y-%m-%d %H:%i:%s') as date, " +
                " a.create_time as sortDate"+
                " from app_tb_neoquestion as a where  exists ( " +
                " select * from app_tb_neogroup as b where b.question_id = a.id " +
                " and b.answer_id = ? " +
                ")  and a.`status` <> 1   " +
                " ORDER BY date DESC " +
                "limit ?,?" ;

        elementType.add(doctorId);
        elementType.add(doctorId);
        elementType.add(doctorId);
        elementType.add((page - 1) * pageSize);
        elementType.add(pageSize);
        List<Map<String, Object>> list = getJt().queryForList(sql, elementType.toArray());
        if (null != list) {
            List<QuestionInfoFormNew> questionInfoFormList = transformatNew(list);
            isAtCurrentDoctor(questionInfoFormList,doctorId);
            return groupAndSortQuestionInfoForm(questionInfoFormList,doctorId);
        }
        return null;
    }

    /*
     * 设置@我的 状态
     */
    public void isAtCurrentDoctor(List<QuestionInfoFormNew> list,String doctorId){
        for(QuestionInfoFormNew qf:list){
            if(StringUtils.isNotBlank(qf.getAssign_answer_id()) && qf.getAssign_answer_id().equals(doctorId)){
                qf.setHasAt(0);
            }
        }
    }

    private Map<String, DoctorAccount> doctorList2Map(List<DoctorAccount> list) {
        Map<String, DoctorAccount> map = new HashMap<>();
        for (DoctorAccount doctor : list) {
            map.put(doctor.getId(), doctor);
        }
        return map;
    }

    /**
     * 1. 我已回答列表顺序：按已追问、已回复、已关闭分组排序。已追问区分已读未读。未读消息会在ui上做出区别（字体和大小）。已追问的消息按追问时间排序，已回复的消息按回复时间排序，已关闭的消息按提问时间排序。
     * 2. 已追问状态，前台显示的时间为追问时间；已回复状态，前台显示的时间为回复时间；已关闭状态，前台显示的时间为提问时间；
     *
     * @param list
     */
    public List<QuestionInfoFormNew> groupAndSortQuestionInfoForm(List<QuestionInfoFormNew> list,String doctorId){
        List<QuestionInfoFormNew> userAppendAskList = new ArrayList<QuestionInfoFormNew>(); //用戶追问
        List<QuestionInfoFormNew> doctorReplyList = new ArrayList<QuestionInfoFormNew>();//医生已回复
        List<QuestionInfoFormNew> closeQuestionList = new ArrayList<QuestionInfoFormNew>(); //问题已关闭
        for(QuestionInfoFormNew qm:list){
            if(qm.getStatus() != 3){

                ReplyGroup lastReply = replyGroupRepository.getCommentGroup(qm.getId(),doctorId);
                if(null != lastReply){
                    qm.setMySortDate(lastReply.getNewCommentTime());
                }
            }

            if(qm.getStatus() == 1){//已回答
                doctorReplyList.add(qm);
            }
            if(qm.getStatus() == 2){ //追问
                userAppendAskList.add(qm);
            }

            if(qm.getStatus() == 3){//关闭
                closeQuestionList.add(qm);
            }
        }


        QuestionInfoFormComparable userAppendAskListSort = new QuestionInfoFormComparable();// true 按照 sortDate 降序排序
        userAppendAskListSort.sortASC = false;
        Collections.sort(userAppendAskList, userAppendAskListSort); //追问，降序

       /* QuestionInfoFormIsReadComparable userAppendAskIsReadSort = new QuestionInfoFormIsReadComparable();// true 按照 isRead 降序排序
        userAppendAskIsReadSort.sortASC = false;
        Collections.sort(userAppendAskList, userAppendAskIsReadSort); //追问，isRead 降序*/


        QuestionInfoFormComparable doctorReplyListSort = new QuestionInfoFormComparable();// true 按照 sortDate 降序排序
        doctorReplyListSort.sortASC = false;
        Collections.sort(doctorReplyList, doctorReplyListSort); //已回答，降序

        QuestionInfoFormComparable closeQuestionListSort = new QuestionInfoFormComparable();// true 按照 sortDate 降序排序
        closeQuestionListSort.sortASC = false;
        Collections.sort(closeQuestionList, closeQuestionListSort); //关闭，降序

        List<QuestionInfoFormNew> sortList = new ArrayList<QuestionInfoFormNew>();
        sortList.addAll(userAppendAskList); //已追问
        sortList.addAll(doctorReplyList); //已回复
        sortList.addAll(closeQuestionList); //已关闭
        return sortList;

    }

    private Map<String, ReplyGroup> groupList2Map(List<ReplyGroup> list) {
        Map<String, ReplyGroup> map = new HashMap<>();
        for (ReplyGroup group : list) {
            map.put(group.getId(), group);
        }
        return map;
    }


    private List<QuestionInfoFormNew> transformatNew(List<Map<String, Object>> param) {
        List<QuestionInfoFormNew> list = new ArrayList<>();
        for (Map<String, Object> map : param) {
            QuestionInfoFormNew qf = new QuestionInfoFormNew(map);
            list.add(qf);
        }
        return list;
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

    public static String formatDate(Date time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(time);
        return dateString;
    }
}
