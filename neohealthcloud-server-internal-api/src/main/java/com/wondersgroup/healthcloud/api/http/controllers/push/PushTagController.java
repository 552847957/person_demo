package com.wondersgroup.healthcloud.api.http.controllers.push;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.push.PushTag;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/22/16.
 */
@RestController
@RequestMapping(path = "/push/tag")
public class PushTagController {

    @Autowired
    private PushTagRepository pushTagRepo;


    @GetMapping(path = "list")
    public String list(@PageableDefault(sort = {"updatetime"},direction = Sort.Direction.DESC) Pageable pageable,
                       @RequestParam(required = false) final String tagname) throws Exception{


        Specification<PushTag> specification = new Specification<PushTag>() {
            @Override
            public Predicate toPredicate(Root<PushTag> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                if(StringUtils.isEmpty(tagname)){
                    return null;
                }
                return criteriaBuilder.like(root.get("tagname").as(String.class), "%"+tagname+"%");
            }
        };

        Page<PushTag> page =  pushTagRepo.findAll(specification,pageable);

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(PushTag.class, new String[]{"tagid","tagname"});
        filterMap.put(PageImpl.class, new String[]{"content", "total_pages", "total_elements", "size", "number", "last"});

        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功",page );

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    @PostMapping(path = "update")
    public JsonResponseEntity addTag(@RequestBody PushTag pushTag) {
        JsonResponseEntity response  = new JsonResponseEntity();
        pushTag.setUpdatetime(new Date());
        pushTag.setId(0);
        pushTagRepo.save(pushTag);
        response.setMsg("保存成功");
        return response;
    }

    @DeleteMapping(path = "/delete")
    public JsonResponseEntity moveClientTag(@RequestParam Integer tagid) {
        JsonResponseEntity response  = new JsonResponseEntity();
        PushTag pushTag = pushTagRepo.findOne(tagid);
        pushTagRepo.delete(pushTag);
        response.setMsg("删除成功");
        return response;
    }
}
