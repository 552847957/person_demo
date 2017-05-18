package com.wondersgroup.healthcloud.api.http.controllers.question;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.helper.push.api.PushClientWrapper;
import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.question.DoctorQuestionService;
import com.wondersgroup.healthcloud.services.question.dto.AllQuestionDetails;
import com.wondersgroup.healthcloud.services.question.dto.DoctorQuestionDetail;
import com.wondersgroup.healthcloud.services.question.dto.QuestionInfoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor/question")
public class DoctorQuestionController {

    @Autowired
    private DoctorQuestionService doctorQuestionService;
    @Autowired
    private PushClientWrapper pushClientWrapper;

    private final int doctor_question_list_size = 10;
    @Autowired
    private DoctorService doctorService;

    @VersionRange
    @RequestMapping(value = "/reply", method = RequestMethod.POST)
    public Object reply(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String content = reader.readString("content", false);
        String contentImgs = reader.readString("contentImgs", true);
        String questionId = reader.readString("questionId", false);
        String doctorId = reader.readString("doctorId", false);

        contentImgs = contentImgs == null ? "" : contentImgs;
        JsonResponseEntity<Object> response = new JsonResponseEntity<>();
        doctorQuestionService.doctorReplay(doctorId, questionId, content, contentImgs);
        //回复成功push用户端
        Question question = doctorQuestionService.queryQuestion(questionId);
        String userId = question.getAskerId();
        String doctorName = doctorService.findDoctorByUid(doctorId).getName();
        AppMessage message = AppMessage.Builder.init().title(doctorName + "医生回复了您的问题").content(content)
                .type(AppMessageUrlUtil.Type.QUESTION).urlFragment(AppMessageUrlUtil.question(questionId)).persistence().build();
        Boolean aBoolean = pushClientWrapper.pushToAlias(message, userId);


        response.setMsg("回复成功!");
        return response;
    }

    @VersionRange
    @RequestMapping(value = "/getTabShow", method = RequestMethod.GET)
    public JsonResponseEntity<Map<String, Object>> list(@RequestParam String doctorId) {
        JsonResponseEntity<Map<String, Object>> responseEntity = new JsonResponseEntity();
        Map<String, Object> info = new HashMap<>();
        List<QuestionInfoForm> list = doctorQuestionService.getDoctorPrivateQuestionLivingList(doctorId, 1, 1);
        int tab = 1;
        if (null != list && !list.isEmpty()) {
            tab = 2;
        }
        info.put("tab", tab);
        responseEntity.setData(info);
        return responseEntity;
    }


    @VersionRange
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonListResponseEntity<QuestionInfoForm> list(@RequestParam String doctorId,
                                                         @RequestParam(required = false, defaultValue = "1") Integer tab,
                                                         @RequestParam(required = false, defaultValue = "1") Integer flag
    ) {
        JsonListResponseEntity<QuestionInfoForm> response = new JsonListResponseEntity<>();
        List<QuestionInfoForm> list;
        if (tab == 1) {
            //已回答
            list = doctorQuestionService.getDoctorPrivateQuestionLivingList(doctorId, flag, doctor_question_list_size);

        } else {
            //问题汇总
            list = doctorQuestionService.getQuestionSquareList(doctorId, flag, doctor_question_list_size);
        }
        Boolean hasMore = false;
        if (list != null && list.size() > doctor_question_list_size) {
            hasMore = true;
            list = list.subList(0, doctor_question_list_size);
        }
        response.setContent(list, hasMore, "", String.valueOf(flag + 1));
        return response;
    }


    @VersionRange
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResponseEntity<DoctorQuestionDetail> getQuestionInfo(@RequestParam String doctorId, @RequestParam String questionId) {
        JsonResponseEntity<DoctorQuestionDetail> response = new JsonResponseEntity<>();
        DoctorQuestionDetail questionDetail = doctorQuestionService.queryQuestionDetail(doctorId, questionId);
        response.setData(questionDetail);
        return response;
    }

    @VersionRange
    @RequestMapping(value = "/hasNewMessage", method = RequestMethod.GET)
    public JsonResponseEntity<Map<String, Integer>> hasNewMessage(@RequestParam String doctorId) {
        JsonResponseEntity<Map<String, Integer>> response = new JsonResponseEntity<>();
        Map<String, Integer> map = new HashMap<>();
        map.put("question", doctorQuestionService.hasNewQuestionForDoctor(doctorId) ? 1 : 0);
        map.put("comment", doctorQuestionService.hasNewCommentForDoctor(doctorId) ? 1 : 0);
        response.setData(map);
        return response;
    }

    /**
     * 问题汇总
     *
     * @param doctorId
     * @param flag
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/allQuestions", method = RequestMethod.GET)
    public JsonListResponseEntity allQuestions(@RequestParam String doctorId,
                                           @RequestParam(required = false, defaultValue = "1") Integer flag) {

        JsonListResponseEntity<QuestionInfoForm> response = new JsonListResponseEntity<>();
        List<QuestionInfoForm> list = doctorQuestionService.getQuestionSquareList(doctorId, flag, doctor_question_list_size);

        Boolean hasMore = false;
        if (list != null && list.size() > doctor_question_list_size) {
            hasMore = true;
            list = list.subList(0, doctor_question_list_size);
        }
        response.setContent(list, hasMore, "", String.valueOf(flag + 1));

        return response;
    }

    /**
     * 问答详情(问题汇总)
     *
     * @param doctorId
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/allQuestionDetails", method = RequestMethod.GET)
    public JsonResponseEntity allQuestionDetails(@RequestParam String doctorId, @RequestParam String questionId) {

        AllQuestionDetails allQuestionDetails = doctorQuestionService.queryAllQuestionDetails(doctorId, questionId);
        return new JsonResponseEntity(0, "操作成功!", allQuestionDetails);
    }

    /**
     * 已回答
     * @param doctorId
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/allRepliedQuestions", method = RequestMethod.GET)
    public JsonListResponseEntity allRepliedQuestions(@RequestParam String doctorId,
                                                  @RequestParam(required = false, defaultValue = "1") Integer flag) {


        JsonListResponseEntity<QuestionInfoForm> response = new JsonListResponseEntity<>();
        List<QuestionInfoForm> list = doctorQuestionService.getQuestionSquareList(doctorId, flag, doctor_question_list_size);

        Boolean hasMore = false;
        if (list != null && list.size() > doctor_question_list_size) {
            hasMore = true;
            list = list.subList(0, doctor_question_list_size);
        }
        response.setContent(list, hasMore, "", String.valueOf(flag + 1));

        return response;
    }


    /**
     * 问答详情(已回答)
     *
     * @param doctorId
     * @param questionId
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/repliedQuestionDetail", method = RequestMethod.GET)
    public JsonResponseEntity repliedQuestionDetail(@RequestParam String doctorId, @RequestParam String questionId) {

        AllQuestionDetails allQuestionDetails = doctorQuestionService.queryAllQuestionDetails(doctorId, questionId);
        return new JsonResponseEntity(0, "操作成功!", allQuestionDetails);
    }

}
