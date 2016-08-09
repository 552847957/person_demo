package com.wondersgroup.healthcloud.api.http.controllers.dic;

import com.wondersgroup.healthcloud.jpa.repository.doctor.DepartGBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shenbin on 16/8/9.
 */
@RestController
@RequestMapping(value = "/api")
public class DepartGBController {

    @Autowired
    private DepartGBRepository departGBRepository;

    /**
     * 查询国标科室代码
     * @return
     */
    @RequestMapping(value = "/departGB", method = RequestMethod.GET)
    public Object findDepartGB(){
        return departGBRepository.findAll();
    }
}
