package com.wondersgroup.healthcloud.api.http.controllers.push;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.helper.push.tag.UserPushTagService;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.push.PushTag;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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
    private UserPushTagService userPushTagService;

    @Autowired
    private PushTagRepository pushTagRepo;

    @PostMapping(path = "/bind")
    public JsonResponseEntity bind(@RequestBody String request) {
        JsonResponseEntity response  = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String tagname = reader.readString("tagname", false);
        String uids = reader.readString("uids", false);
        userPushTagService.bindTag(uids.split(","), tagname);
        response.setMsg("绑定成功");
        return response;
    }

    @PostMapping(path = "/bind/person")
    public String bindPerson(@RequestBody String request) throws Exception{
        JsonKeyReader reader = new JsonKeyReader(request);
        String tagname = reader.readString("tagname", false);
        String info = reader.readString("info", false);
        List<RegisterInfo> list = userPushTagService.bindPerson(info, tagname);
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(RegisterInfo.class, new String[]{"nickname", "personcard", "regmobilephone"});

        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", list);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    @GetMapping(path = "/list")
    public JsonResponseEntity list() throws Exception{
        Specification<PushTag> specification = new Specification<PushTag>() {
            @Override
            public Predicate toPredicate(Root<PushTag> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updatetime").as(Date.class)));
                return criteriaQuery.getRestriction();
            }
        };
        List<PushTag> list =  pushTagRepo.findAll(specification);
        JsonResponseEntity json = new JsonResponseEntity();
        json.setData(list);
        return json;
    }

    @PostMapping(path = "update")
    public JsonResponseEntity update(@RequestBody PushTag pushTag) {
        JsonResponseEntity response  = new JsonResponseEntity();
        PushTag previous = pushTagRepo.findByName(pushTag.getTagname());
        if(null == previous){
            pushTag.setUpdatetime(new Date());
            pushTag = pushTagRepo.save(pushTag);
            response.setData(pushTag);
        }else{
            response.setData(previous);
        }

        return response;
    }
}
