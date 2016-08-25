package com.wondersgroup.healthcloud.services.faq;

import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/16.
 */
public interface FaqService {

    List<Faq> findHomeFaqList();

    /**
     * 根据问题Id查询回答数量
     * @param qId
     * @return
     */
    int countCommentByQid(String qId);

    List<Faq> findFaqList(int pageSize, Integer flag);

    List<Map<String, Object>> findFaqListByQid(String id);

    List<Faq> findQCloseliesByQpidAndDoctorId(String qPid, String doctorId);

    List<Map<String,Object>> findFaqListByPager(int pageNum, int size, Map parameter);

    int countFaqByParameter(Map parameter);

    int showSet(String qId, Integer isShow);

    int countTopQuestion();

    int TopSet(String qId, Integer integer);

    Faq findFaqById(String id);

    void save(Faq faq);

    int updateRootQuestion(Faq faq);

    int saveFirstAnswerByDoctorId(Faq faq);

    Faq findOneFaqByQid(String qId);
}
