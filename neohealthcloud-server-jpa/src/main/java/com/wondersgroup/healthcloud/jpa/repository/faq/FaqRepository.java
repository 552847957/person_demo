package com.wondersgroup.healthcloud.jpa.repository.faq;

import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by longshasha on 16/8/16.
 */
public interface FaqRepository extends JpaRepository<Faq,String> {

    @Query(" select  a from Faq a where a.isShow = 1 and isTop = 1 group by a.qId order by a.askDate desc")
    List<Faq> findTopFaqList();

    @Query(" select  count(a)  from Faq a where a.qId = ?1 and a.qPid is null and a.doctorId is not null")
    int countCommentByQid(String qId);

    @Query(" select  a from Faq a where a.isShow = 1 group by a.qId ")
    List<Faq> findFaqList(Pageable pageable);

    @Query(" select a from Faq a where a.qPid = ?1 and a.doctorId = ?2 order by a.askDate asc ")
    List<Faq> findQCloseliesByQpidAndDoctorId(String qPid, String doctorId);
}
