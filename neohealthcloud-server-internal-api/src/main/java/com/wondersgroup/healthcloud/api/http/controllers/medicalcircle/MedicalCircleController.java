package com.wondersgroup.healthcloud.api.http.controllers.medicalcircle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircle;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenbin on 16/8/28.
 */
@RestController
@RequestMapping("api")
public class MedicalCircleController {

    @Autowired
    private MedicalCircleRepository medicalCircleRepository;

    /**
     * 查询帖子病例列表
     * @param pageable
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "medicalCircle/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findMedicalCircleList(@PageableDefault(size = 20) Pageable pageable) throws JsonProcessingException {
        Page<MedicalCircle> medicalCircles = medicalCircleRepository.findAll(pageable);

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(MedicalCircle.class, new String[]{"id", "title", "content", "is_visible", "tagnames"});
        filterMap.put(PageImpl.class, new String[]{"content", "total_pages", "total_elements", "size", "number", "last"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", medicalCircles);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }
}
