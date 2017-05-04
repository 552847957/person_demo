package com.wondersgroup.healthcloud.api.http.controllers.tube;

import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.TubeRelation;
import com.wondersgroup.healthcloud.services.diabetes.TubeRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhaozhenxing on 2016/12/29.
 */
@RestController
@RequestMapping("/api/tuberelation")
public class TubeRelationController {

    @Autowired
    private TubeRelationService tubeRelationService;

    @WithoutToken
    @RequestMapping(value = "/getTubeRelation", method = RequestMethod.GET)
    public JsonResponseEntity getTubeRelation(@RequestParam(required = false) String registerId,
                                              @RequestParam(required = false) String personCard) {
        if (StringUtils.isEmpty(registerId) && StringUtils.isEmpty(personCard)) {
            return new JsonResponseEntity(1000, "查询参数不能为空");
        }
        TubeRelation tubeRelation;
        if (!StringUtils.isEmpty(registerId)) {
            tubeRelation = tubeRelationService.getTubeRelation(registerId, true);
        } else {
            tubeRelation = tubeRelationService.getTubeRelationByPersonCard(personCard);
        }
        if (tubeRelation != null) {
            return new JsonResponseEntity(0, "", tubeRelation);
        }
        return new JsonResponseEntity(1000, "未查询到在管关系");
    }
}
