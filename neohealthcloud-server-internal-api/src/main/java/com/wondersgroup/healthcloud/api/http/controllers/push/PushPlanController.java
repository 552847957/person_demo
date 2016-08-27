package com.wondersgroup.healthcloud.api.http.controllers.push;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.push.PushPlanDTO;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.exceptions.BadRequestException;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.exceptions.BaseException;
import com.wondersgroup.healthcloud.helper.push.plan.PushPlanService;
import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushPlanRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import com.wondersgroup.healthcloud.services.permission.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by zhuchunliu on 2016/8/26.
 */
@RestController
@RequestMapping("/push/plan")
public class PushPlanController {

    @Autowired
    private PushPlanRepository pushPlanRepo;

    @Autowired
    private PushTagRepository pushTagRepo;

    @Autowired
    private PushPlanService pushPlanService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserRepository userRepo;

    private final String uid = "d8222c3f5f9e11e6bb08000c2918b89b";

    @PostMapping(path = "/list")
    public Pager list(@RequestBody Pager pager) throws Exception{
        pager.getParameter().put("uid",uid);
        Page<PushPlan> page = pushPlanService.findAll(pager.getNumber()-1,pager.getSize(),pager.getParameter());

        List<PushPlanDTO> list = Lists.newArrayList();
        for(PushPlan push : page.getContent()){
            PushPlanDTO dto = new PushPlanDTO(push,uid,permissionService.hasPermission(uid,"push:audit"));
            dto.setTargetName(pushTagRepo.getOne(Integer.parseInt(push.getTarget())).getTagname());
            list.add(dto);
        }
        pager.setData(list);
        pager.setTotalElements((int)page.getTotalElements());
        return pager;
    }

    @PostMapping(path = "/update")
    public JsonResponseEntity list(@RequestBody PushPlan pushPlan) throws Exception{
        pushPlan.setArea(userRepo.findOne(uid).getMainArea());
        pushPlan.setCreator(uid);
        pushPlan.setTarget_type(1);
        pushPlan.setCreateTime(new Date());
        pushPlan.setUpdateTime(new Date());
        pushPlan.setStatus(0);
        pushPlanRepo.save(pushPlan);
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setMsg("保存成功");
        return entity;
    }

    @GetMapping(path = "/info")
    public JsonResponseEntity info(@RequestParam String id) throws Exception{
        PushPlan pushPlan = pushPlanRepo.findOne(Integer.parseInt(id));
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setData(pushPlan);
        return entity;
    }

    /**
     * 通过
     */
    @PostMapping(path = "/pass")
    public JsonResponseEntity pass(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("id", false);
        this.updatPlan(id,1);
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setMsg("通过成功");
        return entity;
    }

    /**
     * 驳回
     */
    @PostMapping(path = "/reject")
    public JsonResponseEntity reject(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("id", false);
        this.updatPlan(id, 4);
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setMsg("驳回成功");
        return entity;
    }

    /**
     * 取消
     */
    @PostMapping(path = "/cancel")
    public JsonResponseEntity cancel(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("id", false);
        this.updatPlan(id,3);
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setMsg("取消成功");
        return entity;
    }

    private void updatPlan(String id ,Integer status){
        PushPlan pushPlan = pushPlanRepo.findOne(Integer.parseInt(id));
        if((1 == status || 4 == status) && 0 != pushPlan.getStatus()){//通过
            throw new BadRequestException(1,"问题非待审核状态");
        }
        pushPlan.setStatus(status);
        pushPlan.setUpdateTime(new Date());
        pushPlanRepo.save(pushPlan);
    }

}
