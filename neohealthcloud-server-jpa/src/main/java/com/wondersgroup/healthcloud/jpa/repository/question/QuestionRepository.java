package com.wondersgroup.healthcloud.jpa.repository.question;

import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, String> {
	
	@Query("select qt from Question qt where qt.askerId=?1")
    List<Question> findByaskId(String userId);
    @Query(nativeQuery = true,value ="SELECT count(*) FROM app_tb_neoquestion WHERE assign_answer_id=?1 AND status<>3 AND is_new_question=1")
    int unreadQuestionuCount(String doctorId);

    @Query(nativeQuery = true,value ="SELECT count(*) FROM app_tb_neoquestion t1 INNER JOIN app_tb_neogroup t2 ON t1.id=t2.question_id " +
             "WHERE t2.answer_id=?1 AND  t1.status<>3 AND t2.has_new_user_comment=1")
    int unreadAskCount(String doctorId);
}
