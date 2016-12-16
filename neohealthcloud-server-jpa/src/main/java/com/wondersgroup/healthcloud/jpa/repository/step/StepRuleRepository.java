package com.wondersgroup.healthcloud.jpa.repository.step;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.step.StepRule;

public interface StepRuleRepository extends JpaRepository<StepRule, String> {

	StepRule findByType(int type);

}
