package com.wondersgroup.healthcloud.jpa.repository.identify;


import com.wondersgroup.healthcloud.jpa.entity.identify.HealthQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author wang
 *
 */
public interface HealthQuestionRepository extends JpaRepository<HealthQuestion, String> {
	@Query(value="select hq from HealthQuestion hq where registerid=?1 and type = 1 order by testtime desc")
	List<HealthQuestion> findResultByRegisterId(String registerid);

	@Query(value="select hq from HealthQuestion hq where registerid=?1 and type = ?2 order by testtime desc")
	List<HealthQuestion> findRecentResultByType(String registerid , String type);

	@Query(value="select count(hq) from HealthQuestion hq where registerid=?1 and type = 1 and delFlag = '0'")
    Integer getTotalIdentify(String registerid);

	@Query(value="select count(hq) from HealthQuestion hq where registerid=?1 and type = 1 and delFlag = '0'")
	Integer getTotalQuestion(String registerid);

	@Query(value="select hq from HealthQuestion hq where registerid=?1 and type = 1 and delFlag = '0'")
	List<HealthQuestion> findQuestionList(String registerid, Pageable pageable);

	@Query(nativeQuery = true,value="select * from app_tb_healthquestion  " +
			" where registerid=?1 and type = 1 and del_flag = '0' order by testtime desc limit 1")
	HealthQuestion findRecent(String registerid);
}
