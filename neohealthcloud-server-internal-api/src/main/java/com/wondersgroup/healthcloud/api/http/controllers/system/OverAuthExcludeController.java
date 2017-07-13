package com.wondersgroup.healthcloud.api.http.controllers.system;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.system.OverAuthExcludes;
import com.wondersgroup.healthcloud.jpa.repository.system.OverAuthExcludesRepository;

@RestController
@RequestMapping("/api/system/overauthExclude")
public class OverAuthExcludeController {
    private static final Logger logger = LoggerFactory.getLogger(OverAuthExcludeController.class);
    
    @Autowired
    private OverAuthExcludesRepository overAuthExcludesRepository;
    
    @Admin
    @GetMapping("/findAll")
    public JsonResponseEntity<List<OverAuthExcludes>> findAll(){
        JsonResponseEntity<List<OverAuthExcludes>> jsonResponseEntity = new JsonResponseEntity<List<OverAuthExcludes>>();
        List<OverAuthExcludes> list = overAuthExcludesRepository.findAll();
        jsonResponseEntity.setData(list);
        return jsonResponseEntity;
    }
    @Admin
    @PostMapping("/saveOrUpdate")
    public JsonResponseEntity<Object> saveOrUpdate(@RequestBody OverAuthExcludes overAuthExclude){
        JsonResponseEntity<Object> jsonResponseEntity = new JsonResponseEntity<Object>();
        try {
            if(overAuthExclude.getId()==null){
                overAuthExcludesRepository.saveAndFlush(overAuthExclude);
                jsonResponseEntity.setMsg("保存成功");
                return jsonResponseEntity;
            }else{
                OverAuthExcludes findOne = overAuthExcludesRepository.findOne(overAuthExclude.getId());
                if(findOne!=null){
                    findOne.setExcludesPath(overAuthExclude.getExcludesPath());
                    findOne.setType(overAuthExclude.getType());
                    overAuthExcludesRepository.save(findOne);
                }
                jsonResponseEntity.setMsg("保存成功");
                return jsonResponseEntity;
            }
        } catch (Exception e) {
            String errorMsg = "保存/修改列表出错";
            logger.error(errorMsg, e);
            jsonResponseEntity.setCode(1000);
            jsonResponseEntity.setMsg(errorMsg);
            
        }
        return jsonResponseEntity;
    }
    
}
