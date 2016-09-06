package com.wondersgroup.healthcloud.api.http.controllers.question;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Response;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.common.http.entity.ResponseWrapper;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.helper.push.api.PushClientWrapper;
import com.wondersgroup.healthcloud.jpa.entity.question.Question;
import com.wondersgroup.healthcloud.jpa.entity.question.Reply;
import com.wondersgroup.healthcloud.jpa.entity.question.ReplyGroup;
import com.wondersgroup.healthcloud.jpa.repository.question.QuestionRepository;
import com.wondersgroup.healthcloud.jpa.repository.question.ReplyGroupRepository;
import com.wondersgroup.healthcloud.services.question.DoctorQuestionService;
import com.wondersgroup.healthcloud.services.question.QuestionService;
import com.wondersgroup.healthcloud.services.question.dto.QuestionDetail;
import com.wondersgroup.healthcloud.services.question.dto.QuestionInfoForm;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.utils.BackCall;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/question")
public class UserQuestionController {

	@Autowired
	private QuestionService questionService;
	@Autowired
	private Environment env;
	@Autowired
	private UserService userService;
	@Autowired
	private PushClientWrapper pushClientWrapper;
	@Autowired
	private HttpRequestExecutorManager httpRequestExecutorManager;
	@Autowired
	private ReplyGroupRepository replyGroupRepository;
	@Autowired
	private QuestionRepository repository;
	/**
	 * 提问
	 * @param question
	 * @return
	 */
	@VersionRange
	@RequestMapping(value="/ask",method= RequestMethod.POST)
	public Object ask(@RequestHeader("main-area") String area,@RequestBody Question question){
		JsonResponseEntity<Object> response=new JsonResponseEntity<>();
		String id="";//问题ID

		String url="";//定时任务

		String doctorId="";//定时任务

		if(!StringUtils.isEmpty(question.getAnswerId())){
			doctorId=question.getAnswerId();
			url=env.getProperty("JOB_CONNECTION_URL")+"/api/jobclient/question/closeOneToOneQuestion?questionId=";
		}else{
			if(area.equals("3101")){
				Map<String, Object> signDoctorByUid = userService.findSignDoctorByUid(question.getAskerId());
				if(signDoctorByUid!=null&&signDoctorByUid.get("id")!=null){
					doctorId=(String)signDoctorByUid.get("id");
					question.setAnswerId(doctorId);
				}
			}
			url=env.getProperty("JOB_CONNECTION_URL")+"/api/jobclient/question/closeQuestion?questionId=";
		}

		id=questionService.saveQuestion(question);
		url+=id;

		if(StringUtils.isNoneEmpty(doctorId)){//一对一问答和向家庭医生提的问题需要推送到医生端
			AppMessage message=AppMessage.Builder.init().title("我的问答").content("您有一条新的问诊提问，点击查看").isDoctor()
					.type(AppMessageUrlUtil.Type.QUESTION).urlFragment(AppMessageUrlUtil.question(id)).persistence().build();
			Boolean aBoolean = pushClientWrapper.pushToAlias(message, doctorId);
			boolean success=aBoolean;
		}
		
		Request request= new RequestBuilder().get().url(url).build();
		httpRequestExecutorManager.newCall(request).callback(new BackCall()).run().as(JsonNodeResponseWrapper.class);
		response.setMsg("您的问题已提交，请耐心等待医生回复");
		return response;
	}

	/**
	 * 问诊列表
	 * @param flag
	 * @param userId
	 * @return
	 */
	@VersionRange
	@RequestMapping(value="/list",method= RequestMethod.GET)
	public Object getQuestionInfoList(@RequestParam(required = false, defaultValue = "0") Integer flag,
            @RequestParam String userId){
		JsonListResponseEntity<QuestionInfoForm> response=new JsonListResponseEntity<>();
		List<QuestionInfoForm> data=questionService.queryQuerstionList(userId, flag);
		
		List<QuestionInfoForm> list=questionService.queryQuerstionList(userId, flag+1);		
		if(null != list&&list.size()>0){
			response.setContent(data, true, "", String.valueOf(flag + 1));
		}else{
			response.setContent(data, false, "", "");
		}
			
		return response;
	}
	/**
	 * 问诊详情
	 * @param questionId
	 * @return
	 */
	@VersionRange
	@RequestMapping(value="/detail",method= RequestMethod.GET)
	public Object getQuestionInfoList(@RequestParam String questionId){
		JsonResponseEntity<Object> response=new JsonResponseEntity<>();
		QuestionDetail data=questionService.queryQuestionDetail(questionId);
		response.setData(data);
		return response;
	}
	/**
	 * 回复医生回答
	 * @param reply
	 * @return
	 */
	@VersionRange
	@RequestMapping(value="/reply",method= RequestMethod.POST)
	public Object reply(@RequestBody Reply reply){
		JsonResponseEntity<Object> response=new JsonResponseEntity<>();

		questionService.saveReplay(reply);		
		ReplyGroup group=questionService.queryAnswerId(reply.getGroupId());
		String doctorId=group.getAnswer_id();

		if(StringUtils.isNoneEmpty(doctorId)){//用户回复医生问题需要推送到医生端
			AppMessage message=AppMessage.Builder.init().title("问答").content("您有一条新的回复，点击查看").isDoctor().param("doctorId",doctorId).param("questionId",group.getQuestion_id())
					.type(AppMessageUrlUtil.Type.QUESTION).urlFragment(AppMessageUrlUtil.question(group.getQuestion_id())).persistence().build();
			Boolean aBoolean = pushClientWrapper.pushToAlias(message, doctorId);
		}
		response.setMsg("回复成功");
		return response;
	}
	/**
	 * 是否有新的未看回复
	 * @param userId
	 * @return
	 */
	@VersionRange
	@RequestMapping(value="/hasNewReply",method= RequestMethod.GET)
	public JsonResponseEntity<Boolean> hasNewReply(@RequestParam String userId){
		JsonResponseEntity<Boolean> response=new JsonResponseEntity<>();
		Boolean is=questionService.queryHasNewReply(userId);
		response.setData(is);
		return response;
	}

	/**
	 * 是否有新的未看回复
	 * @param userId
	 * @return
	 */
	@VersionRange
	@RequestMapping(value="/test",method= RequestMethod.GET)
	public JsonResponseEntity<Boolean> test(@RequestParam String userId){
		JsonResponseEntity<Boolean> response=new JsonResponseEntity<>();

		Question one = repository.findOne(userId);

		return response;
	}

}
