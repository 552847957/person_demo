package com.wondersgroup.healthcloud.services.faq.impl;

import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.jpa.repository.faq.FaqRepository;
import com.wondersgroup.healthcloud.services.faq.FaqService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/16.
 */
@Service
public class FaqServiceImpl implements FaqService {

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private JdbcTemplate jt;


    @Override
    public List<Faq> findHomeFaqList() {
        return faqRepository.findTopFaqList();
    }

    @Override
    public int countCommentByQid(String qId) {
        return faqRepository.countCommentByQid(qId);
    }

    @Override
    public List<Faq> findFaqList(int pageSize, Integer flag) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"isTop"),new Sort.Order(Sort.Direction.DESC,"askDate"));

        return faqRepository.findFaqList(new PageRequest(flag-1, pageSize, sort));
    }

    @Override
    public List<Map<String,Object>> findFaqListByQid(String id) {

        String sql = " select a.id, a.q_id as 'qId',a.asker_name as 'askerName' ,a.gender ,a.age,a.ask_content as 'askContent',a.ask_date as 'askDate', " +
                " a.doctor_id as 'doctorId',d.avatar as 'doctorAvatar',d.`name` as 'doctorName', dd.duty_name as 'dutyName', " +
                " a.answer_content as 'answerContent' , a.answer_date as 'answerDate' " +
                " from faq_question_tb a  " +
                " left join doctor_account_tb d  on a.doctor_id = d.id " +
                " left join doctor_info_tb i on a.doctor_id = i.id " +
                " left join t_dic_duty dd on i.duty_id = dd.duty_id " +
                " where a.q_id = '%s' and a.q_pid is null " +
                " order by a.answer_date asc ";
        sql = String.format(sql,id);
        return jt.queryForList(sql);
    }

    /**
     * 根据跟问题Id查询所有的追问数据
     * @param qPid
     * @param doctorId
     * @return
     */
    @Override
    public List<Faq> findQCloseliesByQpidAndDoctorId(String qPid, String doctorId) {
        return faqRepository.findQCloseliesByQpidAndDoctorId(qPid, doctorId);
    }



    //----------------------后台使用----------------------
    private String faqSql = " select a.id, a.q_id as 'qId', a.asker_name as 'askerName' , a.ask_content as 'askContent', " +
            " a.ask_date as 'askDate',a.gender ,a.age ,a.is_show  as 'isShow', a.is_top as 'isTop'  " +
            " from  faq_question_tb a ";

    @Override
    public List<Map<String, Object>> findFaqListByPager(int pageNum, int size, Map parameter) {

        String sql = faqSql + " where a.q_pid is null "+
                getWhereSqlByParameter(parameter)
                + " GROUP BY a.q_id order by a.is_top desc,a.ask_date desc "
                + " LIMIT " +(pageNum-1)*size +"," + size;
        return jt.queryForList(sql);
    }
    @Override
    public int countFaqByParameter(Map parameter) {
        String sql = "select count(DISTINCT(q_id))  from faq_question_tb a where a.q_pid is null "+
                getWhereSqlByParameter(parameter);
        Integer count = jt.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }


    private String getWhereSqlByParameter(Map parameter) {
        StringBuffer bf = new StringBuffer();
        if(parameter.size()>0){
            if(parameter.containsKey("askerName") &&  StringUtils.isNotBlank(parameter.get("askerName").toString())){
                bf.append(" and a.asker_name like '%"+parameter.get("askerName").toString()+"%' ");
            }
            if(parameter.containsKey("gender") && StringUtils.isNotBlank(parameter.get("gender").toString())){
                bf.append(" and a.gender = "+parameter.get("gender").toString());
            }
            if(parameter.containsKey("age") && StringUtils.isNotBlank(parameter.get("age").toString())){
                bf.append(" and a.age = "+parameter.get("age").toString());
            }
            if(parameter.containsKey("isShow") && StringUtils.isNotBlank(parameter.get("isShow").toString())){
                bf.append(" and a.is_show = "+parameter.get("isShow").toString());
            }
            if(parameter.containsKey("isTop") && StringUtils.isNotBlank(parameter.get("isTop").toString())){
                bf.append(" and a.is_top = "+parameter.get("isTop").toString());
            }
        }
        return bf.toString();
    }

    @Override
    @Transactional
    public int showSet(String qId, Integer isShow) {
        return faqRepository.showSetByQid( qId, isShow);

    }

    @Override
    public int countTopQuestion() {
        String sql = " select count(distinct(a.q_id) ) from faq_question_tb a where a.is_top = 1 and a.q_pid is null";
        Integer count = jt.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    @Transactional
    public int TopSet(String qId, Integer isTop) {
        return faqRepository.topSetByQid(qId, isTop);
    }

    @Override
    public Faq findFaqById(String id) {
        return faqRepository.findOne(id);
    }

    @Override
    public void save(Faq faq) {
        faqRepository.saveAndFlush(faq);
    }

    @Override
    @Transactional
    public int updateRootQuestion(Faq faq) {
        int result = faqRepository.updateRootQuestion(faq.getAskerName(),faq.getGender(),faq.getAge(),faq.getAskContent(),faq.getAskDate(),faq.getQId());
        return result;
    }

    @Override
    @Transactional
    public int saveFirstAnswerByDoctorId(Faq faq) {
        Faq oldFaq = faqRepository.findOne(faq.getId());
        int result = faqRepository.saveFirstAnswerByDoctorId(faq.getDoctorId(),faq.getAnswerContent(),faq.getAnswerDate(),faq.getId());

        int st = faqRepository.updateAllDoctorIdByQpidAndDoctorId(faq.getDoctorId(),oldFaq.getQId(),oldFaq.getDoctorId());

        return result;
    }

    @Override
    public Faq findOneFaqByQid(String qId) {
        return faqRepository.findOneFaqByQid(qId);
    }


}
