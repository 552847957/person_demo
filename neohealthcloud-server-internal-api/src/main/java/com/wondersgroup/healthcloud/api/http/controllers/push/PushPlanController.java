package com.wondersgroup.healthcloud.api.http.controllers.push;

import com.google.common.collect.Lists;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.api.helper.UserHelper;
import com.wondersgroup.healthcloud.api.http.dto.push.PushPlanDTO;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.exceptions.BadRequestException;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.exceptions.BaseException;
import com.wondersgroup.healthcloud.helper.push.plan.PushPlanService;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import com.wondersgroup.healthcloud.jpa.entity.push.PushTag;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushPlanRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import com.wondersgroup.healthcloud.services.permission.PermissionService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
    private UserHelper userHelper;

    @Autowired
    private Environment env;

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    @PostMapping(path = "/list")
    public Pager list(@RequestBody Pager pager) throws Exception{

        User user = userHelper.getCurrentUser();
        Page<PushPlan> page = pushPlanService.findAll(pager.getNumber()-1,pager.getSize(),pager.getParameter(),user);

        List<PushPlanDTO> list = Lists.newArrayList();
        for(PushPlan push : page.getContent()){
            PushPlanDTO dto = new PushPlanDTO(push,user.getUserId(),permissionService.hasPermission(user.getUserId(),"push:audit"));
            if(null != push.getTarget() && 1 == push.getTarget_type()){
                PushTag pushTag = pushTagRepo.getOne(Integer.parseInt(push.getTarget()));
                dto.setTargetName(pushTag.getTagname());
            }
            list.add(dto);
        }
        pager.setData(list);
        pager.setTotalElements((int)page.getTotalElements());
        return pager;
    }

    @PostMapping(path = "/update")
    public JsonResponseEntity list(@RequestBody PushPlan pushPlan) throws Exception{
        User user = userHelper.getCurrentUser();

        pushPlan.setArea(user.getMainArea());
        pushPlan.setCreator(user.getUserId());
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
        Integer id = Integer.parseInt(reader.readString("id", false));
        this.updatPlan(id,1);

        //创建定时任务
        String url=env.getProperty("JOB_CONNECTION_URL")+"/api/healthcloud/push";
        String param = "{\"planId\":\""+id+"\",\"planTime\":\""+new DateTime(pushPlanRepo.findOne(id).getPlanTime()).toString("yyyy-MM-dd HH:mm:ss")+"\"}";
        Request build= new RequestBuilder().post().url(url).body(param).build();
        httpRequestExecutorManager.newCall(build).run().as(JsonNodeResponseWrapper.class);

        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setMsg("审核通过");
        return entity;
    }

    /**
     * 驳回
     */
    @PostMapping(path = "/reject")
    public JsonResponseEntity reject(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        Integer id = Integer.parseInt(reader.readString("id", false));
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
        Integer id = Integer.parseInt(reader.readString("id", false));
        this.updatPlan(id,3);

        //取消定时任务
        String url=env.getProperty("JOB_CONNECTION_URL")+"/api/healthcloud/push";
        Request build= new RequestBuilder().delete().url(url).param("planId", id.toString()).build();
        httpRequestExecutorManager.newCall(build).run().as(JsonNodeResponseWrapper.class);

        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setMsg("取消成功");
        return entity;
    }

    private void updatPlan(Integer id ,Integer status){
        PushPlan pushPlan = pushPlanRepo.findOne(id);
        if((1 == status || 4 == status) && 0 != pushPlan.getStatus()){//通过
            throw new BadRequestException(1001,"问题非待审核状态");
        }
        pushPlan.setStatus(status);
        pushPlan.setUpdateTime(new Date());
        pushPlanRepo.save(pushPlan);
    }

}
