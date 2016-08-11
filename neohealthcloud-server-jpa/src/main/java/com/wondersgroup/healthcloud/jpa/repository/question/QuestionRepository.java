package com.wondersgroup.healthcloud.jpa.repository.question;

import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, String> {
	
	@Query("select qt from Question qt where qt.askerId=?1")
    List<Question> findByaskId(String userId);
}
