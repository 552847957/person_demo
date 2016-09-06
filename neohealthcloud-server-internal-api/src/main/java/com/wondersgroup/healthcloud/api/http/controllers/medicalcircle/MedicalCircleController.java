package com.wondersgroup.healthcloud.api.http.controllers.medicalcircle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircle;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleTag;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleRepository;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleTagRepository;
import com.wondersgroup.healthcloud.services.medicalcircle.MedicalCircleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shenbin on 16/8/28.
 */
@RestController
@RequestMapping("api")
public class MedicalCircleController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MedicalCircleController.class);
    @Autowired
    private MedicalCircleRepository medicalCircleRepository;
    @Autowired
    private MedicalCircleService medicalCircleService;

    @Autowired
    private MedicalCircleTagRepository medicalCircleTagRepository;
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


    /**
     * 医学圈标签列表
     * @param pager
     * @return
     */
    @RequestMapping(value = "/medicalCircleTag/list", method = RequestMethod.POST)
    public Pager tabList(@RequestBody Pager pager){
        int pageNum = 1;
        if(pager.getNumber()!=0)
            pageNum = pager.getNumber();

        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"updateDate"));

        List<MedicalCircleTag> rt = medicalCircleTagRepository.findTagListByPager(new PageRequest((pageNum-1),pager.getSize(),sort));
        int totalSize = medicalCircleTagRepository.countTag();
        pager.setTotalElements(totalSize);
        pager.setData(rt);
        return pager;
    }

    /**
     * 修改医学圈标签
     * @param id
     * @param tagnames
     * @return
     */
    @RequestMapping(value = "medicalCircleTag/update", method = RequestMethod.POST)
    public JsonResponseEntity updateMedicalCircleTag(@RequestParam String id,
                                                     @RequestParam(required = false) String tagnames) {
        MedicalCircle medicalCircle = medicalCircleRepository.findById(id);
        if (medicalCircle != null) {
            medicalCircle.setTagnames(tagnames);
        }
        medicalCircleRepository.save(medicalCircle);
        return new JsonResponseEntity(0, "修改成功");
    }

    /**
     * 批量修改冻结/解冻
     * @param ids
     * @param isVisible
     * @return
     */
    @RequestMapping(value = "medicalCircleIsVisible/update", method = RequestMethod.POST)
    public JsonResponseEntity updateMedicalCircleIsVisible(@RequestParam List<String> ids,
                                                           @RequestParam String isVisible) {
        medicalCircleRepository.updateIsVisible(ids, isVisible);

        return new JsonResponseEntity(0, "修改成功");
    }

    /**
     * 批量修改医学圈标签
     * @param ids
     * @param tagnames
     * @return
     */
    @RequestMapping(value = "medicalCircleTag/batchUpdate", method = RequestMethod.POST)
    public JsonResponseEntity batchUpdateMedicalCircleTag(@RequestParam List<String> ids,
                                                          @RequestParam(required = false) String tagnames) {
        medicalCircleRepository.batchUpdateMedicalCircleTag(ids, tagnames);

        return new JsonResponseEntity(0, "修改成功");
    }

    @RequestMapping(value="medicalCircle/getMedicalCircleById",method = RequestMethod.GET)
    public JsonResponseEntity getMedicalCircleById(@RequestParam String id) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        try {
            MedicalCircle medicalCircle = medicalCircleService.getMedicalCircle(id);
            if (medicalCircle != null) {
                jsonResponseEntity.setData(medicalCircle);
                return jsonResponseEntity;
            } else {
                jsonResponseEntity.setCode(1002);
                jsonResponseEntity.setMsg("未查询到帖子详情");
            }
        } catch (Exception e) {
            String errorMsg = "查询帖子详情出错";
            logger.error(errorMsg, e);
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg(errorMsg);
        }
        return jsonResponseEntity;
    }
}
