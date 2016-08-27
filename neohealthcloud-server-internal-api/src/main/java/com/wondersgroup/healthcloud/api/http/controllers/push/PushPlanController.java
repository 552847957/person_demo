package com.wondersgroup.healthcloud.api.http.controllers.push;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.push.PushPlanDTO;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import com.wondersgroup.healthcloud.jpa.repository.push.PushPlanRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import com.wondersgroup.healthcloud.services.push.PushPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/8/26.
 */
@RestController
@RequestMapping("/plag")
public class PushPlanController {

    @Autowired
    private PushPlanRepository pushPlanRepo;

    @Autowired
    private PushTagRepository pushTagRepo;

    @Autowired
    private PushPlanService pushPlanService;

    @PostMapping(path = "/page")
    public Pager list(Pager pager) throws Exception{
        Page<PushPlan> page = pushPlanService.findAll(pager.getNumber(),pager.getSize(),pager.getParameter());
        List<PushPlanDTO> list = Lists.newArrayList();
        for(PushPlan push : page.getContent()){
            PushPlanDTO dto = new PushPlanDTO(push);
            dto.setTargetName(pushTagRepo.getOne(Integer.parseInt(push.getTarget())).getTagname());
            list.add(dto);
        }
        pager.setData(list);
        pager.setNumber(page.getNumber());
        pager.setTotalPages(page.getTotalPages());
        return pager;
    }

}
