package com.wondersgroup.healthcloud.api.http.controllers.doctorarticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticleCategory;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shenbin on 16/8/30.
 */
@RestController
@RequestMapping(value = "api")
public class DoctorArticleCategoryController {

    @Autowired
    private DoctorArticleCategoryRepository doctorArticleCategoryRepository;

    /**
     * 查询学院分类列表
     * @param pageable
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "doctorArticleCategory/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Admin
    public String findDoctorArticleCategory(@PageableDefault(size = 20, sort = "rank") Pageable pageable) throws JsonProcessingException {
        Page<DoctorArticleCategory> doctorArticleCategories = doctorArticleCategoryRepository.findAll(pageable);

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(DoctorArticleCategory.class, new String[]{"id", "ca_name","rank","update_date", "is_visable"});
        filterMap.put(PageImpl.class, new String[]{"content", "total_pages", "total_elements", "size", "number", "last"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response;
        if (doctorArticleCategories.getContent() != null && !doctorArticleCategories.getContent().isEmpty()) {
            response = new JsonResponseEntity(0, "查询成功", doctorArticleCategories);
        } else {
            response = new JsonResponseEntity(-1, "查询失败");
        }

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 查询学院分类名
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "doctorArticleCategory/name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Admin
    public String findDoctorArticleCategoryName() throws JsonProcessingException {
        List<DoctorArticleCategory> doctorArticleCategories = doctorArticleCategoryRepository.findAll();

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(DoctorArticleCategory.class, new String[]{"id", "ca_name"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response;
        if (doctorArticleCategories != null && !doctorArticleCategories.isEmpty()) {
            response = new JsonResponseEntity(0, "查询成功", doctorArticleCategories);
        } else {
            response = new JsonResponseEntity(-1, "查询失败");
        }

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 查询学院分类详情
     * @param id
     * @return
     */
    @RequestMapping(value = "doctorArticleCategoryDetail", method = RequestMethod.GET)
    @Admin
    public JsonResponseEntity findDoctorArticleCategoryDetail(@RequestParam int id) {
        DoctorArticleCategory doctorArticleCategory = doctorArticleCategoryRepository.findById(id);
        if (doctorArticleCategory != null){
            return new JsonResponseEntity(0, "查询成功", doctorArticleCategory);
        }
        return new JsonResponseEntity(-1, "查询失败");
    }

    /**
     * 保存学院分类
     * @param para
     * @return
     */
    @RequestMapping(value = "saveDoctorArticleCategory", method = RequestMethod.POST)
    @Admin
    public JsonResponseEntity saveDoctorArticleCategory(@RequestBody Map para) {
        DoctorArticleCategory doctorArticleCategory = MapToBeanUtil.fromMapToBean(DoctorArticleCategory.class, para);
        doctorArticleCategory.setUpdateDate(new Date());
        doctorArticleCategoryRepository.save(doctorArticleCategory);

        return new JsonResponseEntity(0, "保存成功");
    }

    /**
     * 设置禁用与启用
     * @param request
     * @return
     */
    @PostMapping(path = "doctorArticleCategory/setVisable")
    @Admin
    public JsonResponseEntity<String> updateCategoryVisable(@RequestBody String request ){
        JsonKeyReader reader = new JsonKeyReader(request);
        int id = reader.readInteger("id",true);
        int isVisable = reader.readInteger("is_visable",true);
        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        int result = doctorArticleCategoryRepository.updateCategoryVisable(id,isVisable);
        if(result<=0){
            response.setCode(2001);
            response.setMsg("设置失败");
            return response;
        }
        response.setMsg("设置成功");
        return response;
    }
}
