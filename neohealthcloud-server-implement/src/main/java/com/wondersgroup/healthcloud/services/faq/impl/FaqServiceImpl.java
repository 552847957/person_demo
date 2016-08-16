package com.wondersgroup.healthcloud.services.faq.impl;

import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.jpa.repository.faq.FaqRepository;
import com.wondersgroup.healthcloud.services.faq.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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

        String sql = " select a.q_id as 'qId',a.asker_name as 'askerName' ,a.gender ,a.age,a.ask_content as 'askContent',a.ask_date as 'askDate', " +
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
}
