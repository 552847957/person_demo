package com.wondersgroup.healthcloud.api.http.controllers.faq;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.faq.DoctorAnswer;
import com.wondersgroup.healthcloud.api.http.dto.faq.FaqDTO;
import com.wondersgroup.healthcloud.api.http.dto.faq.QuestionClosely;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.faq.FaqService;
import com.wondersgroup.healthcloud.services.faq.exception.ErrorNoneFaqException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/17.
 */
@RestController
@RequestMapping(value = "/admin/faq")
public class FaqManageController {

    @Autowired
    private FaqService faqService;

    @Autowired
    private DoctorService doctorService;


    /**
     * 问答集锦列表
     */
    @Admin
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Pager tabList(@RequestBody Pager pager){
        int pageNum = 1;
        if(pager.getNumber()!=0)
            pageNum = pager.getNumber();

        List<Map<String,Object>> mapList = faqService.findFaqListByPager(pageNum,pager.getSize(),pager.getParameter());

        int totalSize = faqService.countFaqByParameter(pager.getParameter());
        pager.setTotalElements(totalSize);
        pager.setData(mapList);
        return pager;
    }

    /**
     * 设置显示不显示
     * @param qId
     * @param isShow
     * @return
     */
    @Admin
    @GetMapping(path = "/showSet")
    public JsonResponseEntity<String> showSet(@RequestParam String qId,
                                              @RequestParam String isShow){
        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        int result = faqService.showSet(qId,Integer.valueOf(isShow));
        if(result<=0){
            response.setCode(2001);
            response.setMsg("设置失败");
            return response;
        }
        response.setMsg("设置成功");
        return response;
    }

    /**
     * 设置是否置顶
     * @param qId
     * @param isTop
     * @return
     */
    @Admin
    @GetMapping(path = "/topSet")
    public JsonResponseEntity<String> topSet(@RequestParam String qId,
                                              @RequestParam String isTop){
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        if("1".equals(isTop)){
            int topCount = faqService.countTopQuestion();
            if(topCount>=2){
                response.setCode(2001);
                response.setMsg("只能置顶两条数据");
                return response;
            }
        }
        int result = faqService.TopSet(qId,Integer.valueOf(isTop));
        if(result<=0){
            response.setCode(2001);
            response.setMsg("设置失败");
            return response;
        }
        response.setMsg("设置成功");
        return response;
    }

    /**
     * 查询问答集锦详情
     * @param id
     * @return
     */
    @GetMapping(value = "/detail")
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


    /**
     * 编辑根问题
     * @param faq
     *
     * id不为空的时候 qId 必传
     *
     * @return
     */
    @Admin
    @PostMapping(path = "/saveRootQuestion")
    public JsonResponseEntity<String> saveRootQuestion(@RequestBody Faq faq){
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        if(StringUtils.isBlank(faq.getId())){
            faq.setId(IdGen.uuid());
            faq.setQId(IdGen.uuid());
            faq.setIsShow(0);
            faq.setIsTop(0);
            faq.setType(0);
            faq.setDelFlag("0");
            faq.setCreateDate(new Date());
            faq.setCreateBy("admin");//todo 保存登录人的id
            faqService.save(faq);
            response.setMsg("保存成功");
            return response;
        }

        if(StringUtils.isBlank(faq.getQId())){
            response.setCode(2001);
            response.setMsg("保存失败-保存主问题需传qId");
            return response;
        }
        int result = faqService.updateRootQuestion(faq);
        if(result<=0){
            response.setCode(2001);
            response.setMsg("保存失败");
            return response;
        }
        response.setMsg("保存成功");
        return response;
    }

    /**
     * 编辑医生对根问题的首次回答
     * @param faq
     *
     * id不为空的时候 qId 必传
     *
     * @return
     */
    @Admin
    @PostMapping(path = "/saveFirstAnswer")
    public JsonResponseEntity<String> saveFirstAnswer(@RequestBody Faq faq){
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        if(StringUtils.isBlank(faq.getId()) || StringUtils.isBlank(faq.getQId()) || StringUtils.isBlank(faq.getDoctorId())){
            response.setCode(2001);
            response.setMsg("保存失败-id/qId/doctorId不能为空");
            return response;
        }
        faq.setUpdateDate(new Date());
        faq.setUpdateBy("admin"); //todo 保存登录人Id
        int result = faqService.saveFirstAnswerByDoctorId(faq);
        if(result<=0){
            response.setCode(2001);
            response.setMsg("保存失败");
            return response;
        }
        response.setMsg("保存成功");
        return response;
    }

    /**
     * 保存追问和回答
     * @param faq
     * @return
     */
    @Admin
    @PostMapping(path = "/saveQuestionClosely")
    public JsonResponseEntity<String> saveQuestionClosely(@RequestBody Faq faq){
        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        if(StringUtils.isBlank(faq.getQPid()) || StringUtils.isBlank(faq.getDoctorId())){
            response.setCode(2001);
            response.setMsg("保存失败-qPid/doctorId不能为空");
            return response;
        }
        if(StringUtils.isBlank(faq.getId())){
            faq.setId(IdGen.uuid());
            faq.setQId(IdGen.uuid());
            faq.setIsShow(0);
            faq.setIsTop(0);
            faq.setType(1);
            faq.setDelFlag("0");
            faq.setCreateDate(new Date());
            faq.setCreateBy("admin");//todo 保存登录人的id
        }
        faq.setUpdateDate(new Date());
        faq.setUpdateBy("admin"); //todo 保存登录人Id
        faqService.save(faq);

        response.setMsg("保存成功");
        return response;
    }


    /**
     * 查询所有医生列表
     * @param kw
     * @return
     */
    @Admin
    @GetMapping(path = "/doctorList")
    public JsonResponseEntity<List<Map<String,Object>>> getDoctorList(@RequestParam(required = false) String kw){
        JsonResponseEntity<List<Map<String,Object>>> response = new JsonResponseEntity<>();

        List<Map<String,Object>> doctorList = doctorService.findAllFaqDoctors(kw);

        response.setData(doctorList);
        return response;
    }

}
