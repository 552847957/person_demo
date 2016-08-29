package com.wondersgroup.healthcloud.api.http.controllers.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.tag.CTag;
import com.wondersgroup.healthcloud.jpa.repository.tag.CTagRepository;
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
 * Created by shenbin on 16/8/29.
 */
@RestController
@RequestMapping("api")
public class CTagController {

    @Autowired
    private CTagRepository cTagRepository;

    /**
     * 查询所有标签(不分页)
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "cTag/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findAllTag() throws JsonProcessingException {
        List<CTag> cTags = cTagRepository.findAll();

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(CTag.class, new String[]{"id", "tagname", "tagmemo", "tagsort", "tagcolor"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", cTags);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 查询所有标签(分页)
     * @param pageable
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "cTags", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findTags(@PageableDefault(size = 20, sort = "tagsort")Pageable pageable) throws JsonProcessingException {
        Page<CTag> cTags = cTagRepository.findAll(pageable);

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(CTag.class, new String[]{"id", "tagname", "tagmemo", "tagsort", "tagcolor"});
        filterMap.put(PageImpl.class, new String[]{"content", "total_pages", "total_elements", "size", "number", "last"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", cTags);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 查询标签详情
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "cTag/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findTagDetail(@RequestParam String id) throws JsonProcessingException {
        CTag cTag = cTagRepository.findById(id);
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(CTag.class, new String[]{"id", "tagname", "tagmemo", "tagsort", "tagcolor"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", cTag);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 保存标签
     * @param para
     * @return
     */
    @RequestMapping(value = "saveCTag", method = RequestMethod.POST)
    public JsonResponseEntity saveCTag(@RequestBody Map para) {
        para.put("id", IdGen.uuid());
        CTag cTag = MapToBeanUtil.fromMapToBean(CTag.class, para);
        cTag.setDelFlag("0");
        cTag.setCreateDate(new Date());
        cTag.setUpdateDate(new Date());
        cTagRepository.save(cTag);

        return new JsonResponseEntity(0, "保存成功");
    }

    /**
     * 修改标签
     * @param para
     * @return
     */
    @RequestMapping(value = "updateCTag", method = RequestMethod.POST)
    public JsonResponseEntity updateCTag(@RequestBody Map para) {
        CTag cTag = MapToBeanUtil.fromMapToBean(CTag.class, para);
        cTag.setDelFlag("0");
        cTag.setCreateDate(new Date());
        cTag.setUpdateDate(new Date());
        cTagRepository.save(cTag);

        return new JsonResponseEntity(0, "修改成功");
    }

    /**
     * 批量删除标签
     * @param ids
     * @return
     */
    @RequestMapping(value = "batchRemoveCTag", method = RequestMethod.DELETE)
    public JsonResponseEntity batchRemoveCTags(@RequestParam List<String> ids) {
        cTagRepository.batchRemoveCTag(ids);

        return new JsonResponseEntity(0, "删除成功");
    }
}
