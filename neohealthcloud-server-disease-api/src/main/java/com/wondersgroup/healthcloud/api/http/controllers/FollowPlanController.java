package com.wondersgroup.healthcloud.api.http.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.api.http.dto.FollowPlanEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.exceptions.RequestPostMissingKeyException;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.BaseInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesService;
import com.wondersgroup.healthcloud.services.diabetes.dto.FollowPlanDTO;
/**
 * 
 * @author zhongshuqing
 *
 */
@RestController
@RequestMapping(value = "/api/followPlan")
public class FollowPlanController {
    @Autowired
    private DiabetesService diabetesService;
    @Autowired
    private DictCache dictCache;

    @Autowired
    private RegisterInfoRepository registerInfoRepo;

    @Autowired
    private BaseInfoRepository baseInfoRepo;

    @GetMapping("/followUp")
    public JsonResponseEntity<FollowPlanEntity> follow(
            @RequestParam(name="registerId",required = false) String uid,
            @RequestParam(name="cardType",required = false) String cardType,
            @RequestParam(name="cardNumber",required = false) String cardNumber){
        
        JsonResponseEntity jsonResponseEntity = this.getInfo(uid,cardType,cardNumber);
        if(0 != jsonResponseEntity.getCode()){
            return jsonResponseEntity;
        }
        cardNumber = jsonResponseEntity.getData().toString();
        cardType = StringUtils.isEmpty(uid)?cardType:"01";
        FollowPlanDTO dto = diabetesService.getFollowPlanList(cardType, cardNumber);
        FollowPlanEntity entity = new FollowPlanEntity(dto);
        return new JsonResponseEntity<>(0,null ,entity);
    }
    
    public JsonResponseEntity getInfo(String uid ,String cardType, String cardNumber){
        if(!StringUtils.isEmpty(uid)){
            RegisterInfo  registerInfo = registerInfoRepo.findOne(uid);
            if(null == registerInfo){
                return new JsonResponseEntity(3001,"用户不存在" ,null);
            }
            if(StringUtils.isEmpty(registerInfo.getPersoncard())){
                return new JsonResponseEntity(3002,"用户尚未进行实名认证" ,null);
            }
            cardNumber = registerInfo.getPersoncard();
        }else{
            if(StringUtils.isEmpty(cardType)){
                throw new RequestPostMissingKeyException("cardType");
            }
            if(StringUtils.isEmpty(cardNumber)){
                throw new RequestPostMissingKeyException("cardNumber");
            }
        }
        JsonResponseEntity response = new JsonResponseEntity();
        response.setData(cardNumber);
        return response;
    }
}
