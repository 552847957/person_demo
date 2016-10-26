package com.wondersgroup.healthcloud.api.http.controllers.game;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.game.DicLight;
import com.wondersgroup.healthcloud.jpa.entity.game.WechatRegister;
import com.wondersgroup.healthcloud.jpa.repository.game.DicLightRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.WechatRegisterRepository;
import com.wondersgroup.healthcloud.services.game.LightService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 微信点亮服务
 * Created by zhuchunliu on 2016/10/20.
 */
@RestController
@RequestMapping(value = "/game/light")
public class LightController {

    @Autowired
    private LightService lightService;

    @Autowired
    private WechatRegisterRepository wechatRegisterRepo;

    @Autowired
    private DicLightRepository dicLightRepo;

    private Logger logger = LoggerFactory.getLogger(LightController.class);

    @GetMapping(path = "/area")
    public JsonResponseEntity area(
            @RequestParam(name = "code",required = false,defaultValue = "310000000000") String code){
        List<Map<String, Object>> list =  lightService.findAreaByParentCode(code);
        return new JsonResponseEntity(0,null,list);
    }


    /**
     * 点灯社区
     * @return
     */
    @PostMapping
    public JsonResponseEntity updateDicLight(@RequestBody String request){


        JsonKeyReader reader = new JsonKeyReader(request);
        String openid = reader.readString("openid",false);
        String code = reader.readString("code",false);

        WechatRegister wechatRegister = wechatRegisterRepo.getByOpenId(openid);
        String registerid = null == wechatRegister ? null : wechatRegister.getRegisterid();

        if(StringUtils.isEmpty(registerid)){
            logger.info("method : updateDicLight  获取不到用户信息 openId : "+openid);
            return new JsonResponseEntity(1001,"获取不到用户信息，openid无效!",null);
        }

        int count = dicLightRepo.findByRegisterId(registerid);
        if(count >=3){
            return new JsonResponseEntity(1002,"机会已经用完，无法为当前社区点亮!",null);
        }

        DicLight dicLight = new DicLight();
        dicLight.setId(IdGen.uuid());
        dicLight.setRegisterid(registerid);
        dicLight.setAreaCode(code);
        dicLight.setCreateDate(new Date());
        dicLight.setUpdateDate(new Date());
        dicLight.setDelFlag("0");
        dicLightRepo.save(dicLight);

        return new JsonResponseEntity(0,"点亮成功!",ImmutableMap.of("count",dicLightRepo.getTotal()));
    }

    /**
     * 点灯社区
     * @return
     */
    @GetMapping
    public JsonResponseEntity getDicLight(@RequestParam(name="openid",required = true) String openid){

        WechatRegister wechatRegister = wechatRegisterRepo.getByOpenId(openid);
        String registerid = null == wechatRegister ? null : wechatRegister.getRegisterid();

        if(StringUtils.isEmpty(registerid)){
            logger.info("method : getDicLight  获取不到用户信息 openId : "+openid);
            return new JsonResponseEntity(1001,"获取不到用户信息!",null);
        }

        List<Map<String,Object>> list = lightService.getDicLight(registerid);

        return new JsonResponseEntity(0,null,this.getDicLightInfo(list));
    }



    @GetMapping("/statistic")
    public JsonResponseEntity statistic(@RequestParam(name = "code",required = false,defaultValue = "310000000000") String code){
        List<Map<String, Object>> list =  lightService.statistic(code);
        return new JsonResponseEntity(0,null,list);
    }

    @GetMapping("/share")
    public JsonResponseEntity share(@RequestParam(name="openid",required = true) String openid){

        WechatRegister wechatRegister = wechatRegisterRepo.getByOpenId(openid);
        String registerid = null == wechatRegister ? null : wechatRegister.getRegisterid();

        if(StringUtils.isEmpty(registerid)){
            logger.info("method : getDicLight  获取不到用户信息 openId : "+openid);
            return new JsonResponseEntity(1001,"获取不到用户信息!",null);
        }

        Map<String,Object> map = lightService.getRecentDicLight(registerid);
        if(null == map){
            return new JsonResponseEntity(1002,"尚未点亮任何街道!",null);
        }
        map.put("count",dicLightRepo.getTotal());

        return new JsonResponseEntity(0,null,map);
    }

    private List<ImmutableMap> getDicLightInfo(List<Map<String,Object>> info){

        Map<String,Integer> map = Maps.newHashMap();
        for(Map<String,Object> obj : info){
            if(map.containsKey(obj.get("area_code"))){
                map.put(obj.get("area_code").toString(),map.get(obj.get("area_code"))+1);
            }else{
                map.put(obj.get("area_code").toString(),1);
            }
        }

        List<ImmutableMap> list = Lists.newArrayList();
        for(Map<String,Object> obj : info){
            if(map.isEmpty()){
                break;
            }
            if(!map.containsKey(obj.get("area_code"))){
                continue;
            }
            list.add(ImmutableMap.of("streetName", obj.get("streetName"), "areaName", obj.get("areaName"), "count", map.get(obj.get("area_code"))));
            map.remove(obj.get("area_code"));
        }

        return list;
    }
}
