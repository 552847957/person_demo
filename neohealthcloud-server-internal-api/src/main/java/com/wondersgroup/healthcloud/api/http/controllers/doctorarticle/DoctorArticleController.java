package com.wondersgroup.healthcloud.api.http.controllers.doctorarticle;

import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shenbin on 16/8/30.
 */
@RestController
@RequestMapping(value = "api")
public class DoctorArticleController {

    @Autowired
    private DoctorArticleRepository doctorArticleRepository;

}
