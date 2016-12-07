package com.wondersgroup.healthcloud.services.step;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.step.StepRule;
import com.wondersgroup.healthcloud.jpa.repository.step.StepRuleRepository;

@Service
@Transactional
public class StepRuleService {

	@Autowired
	StepRuleRepository stepRuleRepository;

	public void save(StepRule rule) {
		StepRule ruleTb = stepRuleRepository.findByType(rule.getType());
		if(ruleTb != null){
			stepRuleRepository.delete(ruleTb);
		}
		
		rule.setId(IdGen.uuid());
		rule.setCreateTime(new Date());
		rule.setUpdateTime(rule.getCreateTime());
		stepRuleRepository.save(rule);
	}

	public StepRule findByType(int type) {
		return stepRuleRepository.findByType(type);
	}

}
