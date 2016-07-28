package com.wondersgroup.healthcloud.jpa.repository.user;


import com.wondersgroup.healthcloud.jpa.entity.user.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, String> {

}
