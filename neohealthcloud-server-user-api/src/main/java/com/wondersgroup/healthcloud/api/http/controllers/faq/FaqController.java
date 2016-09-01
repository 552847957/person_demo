package com.wondersgroup.healthcloud.api.http.controllers.faq;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.faq.DoctorAnswer;
import com.wondersgroup.healthcloud.api.http.dto.faq.FaqDTO;
import com.wondersgroup.healthcloud.api.http.dto.faq.QuestionClosely;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.services.faq.FaqService;
import com.wondersgroup.healthcloud.services.faq.exception.ErrorNoneFaqException;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/16.
 */
@RestController
@RequestMapping(value = "/api")
public class FaqController {

    @Autowired
    private FaqService faqService;


    /**
     * 更多 问答集锦
     * @param flag
     * @return
     */
    @VersionRange
    @WithoutToken
    @RequestMapping(value = "/faq/list", method = RequestMethod.GET)
    public JsonListResponseEntity getFaqList(@RequestParam(required = false, defaultValue = "1") Integer flag){
        JsonListResponseEntity<FaqDTO> body = new JsonListResponseEntity<>();
        int pageSize = 10;
        boolean has_more = false;
        List<FaqDTO> list = Lists.newArrayList();

        List<Faq> faqList = faqService.findFaqList(pageSize,flag);

        if(null != faqList && faqList.size() == pageSize){
            if(null != faqService.findFaqList(pageSize,flag+1)){
                has_more = true;
                flag = flag +1;
            }
        }

        if(faqList !=null){
            for (Faq faq : faqList){
                FaqDTO faqDTO = new FaqDTO(faq);
                faqDTO.setAskTime(DateFormatter.questionListDateFormat(faq.getAskDate()));
                //查询回答数
                int commentCount = faqService.countCommentByQid(faq.getQId());
                faqDTO.setCommentCount(commentCount);
                list.add(faqDTO);
            }
        }
        if (has_more) {
            body.setContent(list, true, null, String.valueOf(flag));
        } else {
            body.setContent(list, false, null, null);
        }
        return body;

    }

    /**
     * 查询问答集锦详情
     * @param id
     * @return
     */
    @VersionRange
    @WithoutToken
    @RequestMapping(value = "/faq/detail", method = RequestMethod.GET)
    public JsonResponseEntity<FaqDTO> getFaqDetail(@RequestParam(required = true) String id){
        JsonResponseEntity<FaqDTO> response = new JsonResponseEntity<>();
        List<Map<String, Object>> faqList = faqService.findFaqListByQid(id);
        if(faqList == null){
            throw new ErrorNoneFaqException();
        }
        FaqDTO question = new FaqDTO(faqList.get(0));
        question.setCommentCount(faqService.countCommentByQid(question.getId()));

        List<DoctorAnswer> doctorAnswers = Lists.newArrayList();

        for(Map<String, Object> faq : faqList){
            DoctorAnswer doctorAnswer = new DoctorAnswer(faq);
            String qPid = faq.get("qid").toString();
            String doctorId = faq.get("doctorId")==null?"":faq.get("doctorId").toString();
            if(StringUtils.isNotBlank(doctorId)){
                List<Faq> qCloselies = faqService.findQCloseliesByQpidAndDoctorId(qPid,doctorId);
                List<QuestionClosely> questionCloselies = Lists.newArrayList();
                if(qCloselies !=null){
                    for(Faq faqClosely: qCloselies){
                        QuestionClosely  questionClosely = new QuestionClosely(faqClosely);
                        questionCloselies.add(questionClosely);
                    }
                    doctorAnswer.setQuestionCloselies(questionCloselies);
                }

            }
            doctorAnswers.add(doctorAnswer);

        }
        question.setDoctorAnswers(doctorAnswers);
        response.setData(question);
        return response;
    }



}
