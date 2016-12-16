package com.wondersgroup.healthcloud.api.http.controllers.step;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.step.StepRule;
import com.wondersgroup.healthcloud.services.step.StepRuleService;

@RestController
@RequestMapping("/api/step/rule")
public class StepRuleController {

	@Autowired
	StepRuleService stepRuleService;

	@RequestMapping(method = RequestMethod.POST)
	public Object save(@RequestBody List<StepRule> rule) {
		JsonResponseEntity<String> responseEntity = new JsonResponseEntity<>();
		stepRuleService.save(rule);
		responseEntity.setMsg("新增成功");
		return responseEntity;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Object findByType(int type) {
		JsonResponseEntity<StepRule> responseEntity = new JsonResponseEntity<>();
		StepRule rule = stepRuleService.findByType(type);
		responseEntity.setData(rule);
		return responseEntity;

	}
}
