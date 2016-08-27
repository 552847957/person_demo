package com.wondersgroup.healthcloud.api.http.controllers.push;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.push.PushPlanDTO;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.helper.push.plan.PushPlanService;
import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import com.wondersgroup.healthcloud.jpa.repository.push.PushPlanRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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
        String uid = pager.getParameter().get("uid").toString();

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

    @PostMapping(path = "/update")
    public JsonResponseEntity list(PushPlan pushPlan) throws Exception{
        pushPlan.setCreateTime(new Date());
        pushPlanRepo.save(pushPlan);
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setMsg("保存成功");
        return entity;
    }

    /**
     * 审核 , 驳回 ，取消
     * @param
     * @return
     * @throws Exception
     */
    @PostMapping(path = "/operate")
    public JsonResponseEntity audit(@RequestBody String request) throws Exception{
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("id", false);
        String status = reader.readString("status", false);

        PushPlan pushPlan = pushPlanRepo.findOne(Integer.parseInt(id));
        pushPlan.setStatus(Integer.parseInt(status));
        pushPlan.setUpdateTime(new Date());
        pushPlanRepo.save(pushPlan);
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setMsg("保存成功");
        return entity;
    }

}
