package com.wondersgroup.healthcloud.jpa.repository.faq;

import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by longshasha on 16/8/16.
 */
public interface FaqRepository extends JpaRepository<Faq,String> {

    @Query(" select  a from Faq a where a.isShow = 1 and a.isTop = 1 and a.qPid is null  group by a.qId order by a.askDate desc")
    List<Faq> findTopFaqList();

    @Query(" select  count(a)  from Faq a where a.qId = ?1 and a.qPid is null and a.doctorId is not null")
    int countCommentByQid(String qId);

    @Query(" select  a from Faq a where a.isShow = 1 and a.qPid is null group by a.qId ")
    List<Faq> findFaqList(Pageable pageable);

    @Query(" select a from Faq a where a.qPid = ?1 and a.doctorId = ?2 order by a.askDate asc ")
    List<Faq> findQCloseliesByQpidAndDoctorId(String qPid, String doctorId);

    @Modifying
    @Query(" update Faq a set a.isShow = ?2 where a.qId = ?1")
    int showSetByQid(String qId, Integer isShow);

    @Modifying
    @Query(" update Faq a set a.isTop = ?2 where a.qId = ?1")
    int topSetByQid(String qId, Integer isTop);

    @Modifying
    @Query(" update Faq a set a.askerName=?1 ,a.gender=?2,a.age=?3,a.askContent=?4,a.askDate=?5 where a.qId = ?6  ")
    int updateRootQuestion(String askerName, Integer gender, Integer age, String askContent, Date askDate, String qId);

    @Modifying
    @Query(" update Faq a set a.doctorId=?1 ,a.answerContent=?2,a.answerDate=?3  where a.qId = ?4  ")
    int saveFirstAnswerByDoctorId(String doctorId, String answerContent, Date answerDate, String id);
}
