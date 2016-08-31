package com.wondersgroup.healthcloud.api.http.controllers.doctorarticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenbin on 16/8/30.
 */
@RestController
@RequestMapping(value = "api")
public class DoctorArticleController {

    @Autowired
    private DoctorArticleRepository doctorArticleRepository;

    /**
     * 查询学院文章列表
     * @param pageable
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "doctorArticle/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findDoctorArticle(@PageableDefault(size = 20) Pageable pageable) throws JsonProcessingException {
        Page<DoctorArticle> doctorArticles = doctorArticleRepository.findAll(pageable);

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(DoctorArticle.class, new String[]{"id", "title", "update_time", "is_visable"});
        filterMap.put(PageImpl.class, new String[]{"content", "total_pages", "total_elements", "size", "number", "last"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response;
        if (doctorArticles.getContent() != null && !doctorArticles.getContent().isEmpty()) {
            response = new JsonResponseEntity(0, "查询成功", doctorArticles);
        } else {
            response = new JsonResponseEntity(-1, "查询失败");
        }

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 查询学院文章详情
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "doctorArticleDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findDoctorArticleDetail(@RequestParam int id) throws JsonProcessingException {
        DoctorArticle doctorArticle = doctorArticleRepository.findById(id);

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(DoctorArticle.class, new String[]{"id", "title", "category_ids", "brief", "is_visable", "thumb", "content"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response;
        if (doctorArticle != null) {
            response = new JsonResponseEntity(0, "查询成功", doctorArticle);
        } else {
            response = new JsonResponseEntity(-1, "查询失败");
        }

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 保存学院文章
     * @param para
     * @return
     */
    @RequestMapping(value = "saveDoctorArticle", method = RequestMethod.POST)
    public JsonResponseEntity saveDoctorArticle(@RequestBody Map para) {
        DoctorArticle doctorArticle = MapToBeanUtil.fromMapToBean(DoctorArticle.class, para);
        doctorArticle.setUpdateTime(new Date());
        doctorArticleRepository.save(doctorArticle);

        return new JsonResponseEntity(0, "保存成功");
    }

    /**
     * 修改学院文章
     * @param para
     * @return
     */
    @RequestMapping(value = "updateDoctorArticle", method = RequestMethod.POST)
    public JsonResponseEntity updateDoctorArticle(@RequestBody Map para) {
        DoctorArticle doctorArticle = MapToBeanUtil.fromMapToBean(DoctorArticle.class, para);
        doctorArticle.setUpdateTime(new Date());
        doctorArticleRepository.save(doctorArticle);

        return new JsonResponseEntity(0, "修改成功");
    }
}