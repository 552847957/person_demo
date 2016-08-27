package com.wondersgroup.healthcloud.api.http.controllers.push;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.entity.push.PushTag;
import com.wondersgroup.healthcloud.jpa.entity.push.UserPushTag;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.UserPushTagRepository;
import com.wondersgroup.healthcloud.services.push.PushTagService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.criteria.OrderImpl;
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
import java.util.List;
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
@RequestMapping(path = "/tag")
public class PushTagController {

    @Autowired
    private PushTagService pushTagService;

    @Autowired
    private PushTagRepository pushTagRepo;

    @PostMapping(path = "/bind")
    public JsonResponseEntity bind(@RequestBody String request) {
        JsonResponseEntity response  = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String tagname = reader.readString("tagname", false);
        String uids = reader.readString("uids", false);
        Boolean flag = pushTagService.bindTag(tagname, uids);
        if(flag) {
            response.setMsg("绑定成功");
        }else{
            response.setCode(1001);
            response.setMsg("绑定失败");
        }
        return response;
    }

    @GetMapping(path = "/list")
    public String list() throws Exception{

        List<PushTag> list =  pushTagService.findAll();
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(PushTag.class, new String[]{"tagid","tagname"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功",list );
        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    @PostMapping(path = "update")
    public JsonResponseEntity update(@RequestBody PushTag pushTag) {
        JsonResponseEntity response  = new JsonResponseEntity();
        pushTag.setUpdatetime(new Date());
        pushTagRepo.save(pushTag);
        response.setMsg("保存成功");
        return response;
    }
}
