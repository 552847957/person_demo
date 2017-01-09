package com.wondersgroup.healthcloud.api.http.controllers.spread;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.spread.Evangelist;
import com.wondersgroup.healthcloud.jpa.repository.spread.EvangelistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by nick on 2016/12/23.
 */
@RestController
@RequestMapping("/api/evangelist")
public class SpreadController {

    @Autowired
    private EvangelistRepository evangelistRepository;

    @PostMapping(path = "new")
    public JsonResponseEntity saveEvangelist(@RequestBody Evangelist evangelist){
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        Evangelist another = evangelistRepository.findBySpreadCodeAndName(evangelist.getSpreadCode(), evangelist.getName());
        if(another!=null){
            throw new CommonException(1000,"您填写的邀请码️已存在");
        }
        evangelist.setId(IdGen.uuid());
        evangelist.setCreateTime(new Date());
        evangelistRepository.save(evangelist);
        return responseEntity;
    }
}
