package com.wondersgroup.healthcloud.api.http.controllers.push;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.api.http.dto.push.PushPlanDTO;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.exceptions.BadRequestException;
import com.wondersgroup.healthcloud.common.http.exceptions.RequestPostMissingKeyException;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.AppUrlSchemaUtils;
import com.wondersgroup.healthcloud.exceptions.BaseException;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.helper.push.api.PushClientWrapper;
import com.wondersgroup.healthcloud.helper.push.plan.PushPlanService;
import com.wondersgroup.healthcloud.jpa.constant.AppPushConstant;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import com.wondersgroup.healthcloud.jpa.entity.push.PushTag;
import com.wondersgroup.healthcloud.jpa.repository.article.NewsArticleRepo;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushPlanRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import com.wondersgroup.healthcloud.services.permission.PermissionService;

/**
 * Created by zhuchunliu on 2016/8/26.
 */
@RestController
@RequestMapping("/push/plan")
public class PushPlanController {

    private Logger logger = LoggerFactory.getLogger(PushPlanController.class);

    @Autowired
    private PushPlanRepository pushPlanRepo;

    @Autowired
    private PushTagRepository pushTagRepo;

    @Autowired
    private PushPlanService pushPlanService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    @Autowired
    private PushClientWrapper pushClientWrapper;

    @Value("${JOB_CONNECTION_URL}")
    private String jobClientUrl;

    @Value("${h5-web.connection.url}")
    private String h5Url;

    @Autowired
    private NewsArticleRepo articleRepo;

    @Autowired
    private UserRepository userRepo;
    //話題定时URL
    public static String TOPIC_URL=AppUrlSchemaUtils.getBasePath(AppUrlSchemaUtils.areaCode);
    
    @PostMapping(path = "/list")
    public Pager list(@RequestBody Pager pager) throws Exception{

        if(null == pager.getParameter() || !pager.getParameter().containsKey("uid")
                || StringUtils.isEmpty(pager.getParameter().get("uid").toString())){
            throw new RequestPostMissingKeyException("uid");
        }
        User user = userRepo.findOne(pager.getParameter().get("uid").toString());
        if(null == user){
            throw new BaseException(1000,"用户尚未登录，请先登录",null){

            };
        }
//        Map<String, Object> params = pager.getParameter();
//        params.put("type", AppPushConstant.PushType.ARTICLE);
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
        if(StringUtils.isEmpty(pushPlan.getCreator())){
            throw new RequestPostMissingKeyException("creator");
        }
        User user = userRepo.findOne(pushPlan.getCreator());

        if(null == user){
            throw new BaseException(1000,"用户尚未登录，请先登录",null){

            };
        }

        pushPlan.setArea(user.getMainArea());
        pushPlan.setCreator(user.getUserId());
        pushPlan.setTarget_type(1);
        pushPlan.setType(pushPlan.getType());
        pushPlan.setCreateTime(new Date());
        pushPlan.setUpdateTime(new Date());
        pushPlan.setStatus(0);
        if(null!=pushPlan.getTopicId()){
            pushPlan.setTopicId(pushPlan.getTopicId());
            pushPlan.setUrl(AppUrlSchemaUtils.bbsTopicView(pushPlan.getTopicId()));
        }
        if(null != pushPlan.getArticleId()){
            pushPlan.setUrl(h5Url+"/article/detail?id="+pushPlan.getArticleId()+"&area="+pushPlan.getArea()+"&for_type=article");
        }
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

        logger.error("开始创建push定时任务，pushId :"+id);
        PushPlan pushPlan = pushPlanRepo.findOne(id);
        //创建定时任务
        String param = "{\"planId\":\""+id+"\",\"planTime\":\""+new DateTime(pushPlan.getPlanTime()).toString("yyyy-MM-dd HH:mm:ss")+"\"}";
        Request build=null;
        if(pushPlan.getType()==AppPushConstant.PushType.ARTICLE){
            build = new RequestBuilder().post().url(jobClientUrl+"/api/healthcloud/push").body(param).build();
        }else if(pushPlan.getType()==AppPushConstant.PushType.TOPIC){
            build = new RequestBuilder().post().url(TOPIC_URL+"/api/healthcloud/push").body(param).build();
        }
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(build).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();

        JsonResponseEntity entity = new JsonResponseEntity();
        if(0 == result.get("code").asInt()){
            entity.setMsg("审核通过");
            logger.error("定时任务(pushId = "+id+")创建成功，返回结果"+result);
        }else{
            this.updatPlan(id,0);
            logger.error("定时任务(pushId = "+id+")创建错误，返回结果"+result);
            entity.setMsg("定时任务创建出错，适合失败");
        }


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
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        Integer id = Integer.parseInt(reader.readString("id", false));
        Integer preStatus = pushPlanRepo.findOne(id).getStatus();
        try {
            String message = pushPlanService.cancel(id,preStatus,jobClientUrl,TOPIC_URL,3);
            entity.setMsg(message);
            return entity;
        } catch (Exception e) {
            String errorMsg = "取消出错!";
            logger.error(errorMsg, e);
            entity.setCode(1001);
            entity.setMsg(errorMsg);
        }
        return entity;
    }

    @PostMapping(path = "/send")
    public String send(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        Integer planId = reader.readDefaultInteger("planId", 0);
        PushPlan pushPlan = pushPlanRepo.findOne(planId);
        if(null != pushPlan){
            AppMessage message = AppMessage.Builder.init().title(pushPlan.getTitle()).content(pushPlan.getContent()).
                    urlFragment(pushPlan.getUrl()).
                    type(AppMessageUrlUtil.Type.HTTP).build();
            if(null == pushPlan.getTarget()){
                pushClientWrapper.pushToAll(message,pushPlan.getArea());
            }else{
                pushClientWrapper.pushToTags(message,pushPlan.getArea(), ImmutableList.of(pushPlan.getTarget()));
            }
        }
        pushPlan.setStatus(2);
        pushPlan.setUpdateTime(new Date());
        pushPlanRepo.save(pushPlan);
        return "{\"code\":0}";
    }

    @GetMapping(path = "/article")
    public JsonResponseEntity article(@RequestParam(name = "articleId",required = true) Integer articleId,
                                      @RequestParam(name = "area",required = false) String area) {
        JsonResponseEntity reponse = new JsonResponseEntity();
        NewsArticle article = articleRepo.queryArticleById(articleId);
        if(null != article) {
            reponse.setData(ImmutableMap.of("url", h5Url + "/article/detail?id=" + articleId+"&area="+area+"&for_type=article",
                    "title",article.getTitle(),"content",article.getBrief()));
        }
        return reponse;
    }

    private PushPlan updatPlan(Integer id ,Integer status){
        PushPlan pushPlan = pushPlanRepo.findOne(id);
        if((1 == status || 4 == status) && 0 != pushPlan.getStatus()){//通过
            throw new BadRequestException(1001,"问题非待审核状态");
        }
        pushPlan.setStatus(status);
        pushPlan.setUpdateTime(new Date());
        pushPlanRepo.save(pushPlan);
        return pushPlan;
        
    }
    
    
}