package com.wondersgroup.healthcloud.api.http.controllers.assessment;

import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.repository.assessment.AssessmentRepository;
import com.wondersgroup.healthcloud.services.assessment.AssessmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhuchunliu on 2017/6/14.
 */
@Component
public class DiseaseConfig implements CommandLineRunner {

    @Autowired
    private AssessmentRepository assessmentRepo;

    @Autowired
    private AssessmentService assessmentService;

    @Override
    public void run(String... strings) throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Assessment> list = assessmentRepo.findUnSyncAssessment();
                if(0 == list.size()) return;
                for(Assessment assessment : list){
                    String assment = assessmentService.getResult(assessment);
                    if(!StringUtils.isEmpty(assment)){
                        StringBuffer buffer = new StringBuffer();
                        if(assment.contains("1-")) buffer.append(",1");
                        if(assment.contains("2-")) buffer.append(",2");
                        if(assment.contains("3-")) buffer.append(",3");
                        assessment.setResult(buffer.toString().substring(1));
                        assessmentRepo.save(assessment);
                    }
                }
            }
        }).start();


    }

}