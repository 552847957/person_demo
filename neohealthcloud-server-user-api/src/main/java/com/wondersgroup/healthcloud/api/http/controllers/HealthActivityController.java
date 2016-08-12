package com.wondersgroup.healthcloud.api.http.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.activiti.HealthActivityInfo;
import com.wondersgroup.healthcloud.jpa.repository.activiti.HealthActivityDetailRepository;
import com.wondersgroup.healthcloud.jpa.repository.activiti.HealthActivityInfoRepository;
import com.wondersgroup.healthcloud.services.user.HealthActivityInfoService;
import com.wondersgroup.healthcloud.services.user.dto.healthactivity.HealthActivityAPIEntity;

@RestController
@RequestMapping("/api")
public class HealthActivityController {
	@Autowired
	private HealthActivityInfoService haiService;

//	@Autowired
//	private ScoreCache scoreCache;

	@Autowired
	private HealthActivityInfoRepository healthActivityRepository;

	@Autowired
	private HealthActivityDetailRepository healthActivityDetailRepository;

//	@Autowired
//	private DictCache dictCache; 
	/**
	 * 根据类型和区域查询健康活动
	 * @param registerid
	 * @param area
	 * @param type
	 * @return
	 */
//	@RequestMapping(value = "/activities", method = RequestMethod.GET,headers = {"version=2.[234].*"})
//	@ResponseBody
//	public JsonResponseEntity<List<HealthActivityAPIEntity>> getHealthActivityList(
//			@RequestParam(value = "uid",required = false) String registerid,
//			@RequestParam(value = "location", required = true) String area,
//			@RequestParam(value = "type", required = true) String type,
//			@RequestHeader(value="screen-width")String width,
//			@RequestHeader(value="screen-height")String height,HttpServletRequest request) {
//
//			JsonResponseEntity<List<HealthActivityAPIEntity>> response = new JsonResponseEntity<List<HealthActivityAPIEntity>>();
//			List<HealthActivityAPIEntity> list = new ArrayList<HealthActivityAPIEntity>();
//			
//			List<HealthActivityInfo> infoList = haiService.getHealthActivityInfos(area, type);
//			for (HealthActivityInfo info : infoList) {
//				
//				HealthActivityAPIEntity entity = new HealthActivityAPIEntity(info,width,height);
//				this.setDetailInfo(entity,info,registerid);
//				list.add(entity);
//			}
//			response.setData(list);
//			return response;
//	}

	/**
	 * 根据类型和区域查询健康活动【分页功能】
	 * @param registerid
	 * @param area
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/activities/page", method = RequestMethod.GET)
	@VersionRange
	public JsonListResponseEntity<HealthActivityAPIEntity> getHealthActivityPageList(
			@RequestParam(value = "uid",required = false) String registerid,
			@RequestParam(value = "location", required = true) String area,
			@RequestParam(value = "type", required = true) String type,
			@RequestParam(value = "flag", required = false) String flag,
			@RequestHeader(value="screen-width")String width,
			@RequestHeader(value="screen-height")String height,HttpServletRequest request) {

		int pageNo = 1, pageSize = 10;
		JsonObject json = null;
		if (!StringUtils.isEmpty(flag)) {
			json = new Gson().fromJson(flag, JsonObject.class);
			pageNo = json.get("pageNo").getAsInt();
			pageSize = json.get("pageSize").getAsInt();
		}



		List<HealthActivityInfo> infoList = haiService.getHealthActivityInfos(area, type,pageNo,pageSize);
		Boolean more = false;
		if (infoList.size() > pageSize) {
			infoList.remove(pageSize);
			more = true;
			if (null == json) {
				json = new JsonObject();
				json.addProperty("pageNo", "2");
				json.addProperty("pageSize", "10");
			} else {
				json.addProperty("pageNo", pageNo + 1);
				json.addProperty("pageSize", "10");
			}
		}

		List<HealthActivityAPIEntity> list = new ArrayList<HealthActivityAPIEntity>();
		for (HealthActivityInfo info : infoList) {
			HealthActivityAPIEntity entity = new HealthActivityAPIEntity(info,width,height);
			this.setDetailInfo(entity,info,registerid);
			list.add(entity);
		}

		JsonListResponseEntity<HealthActivityAPIEntity> response = new JsonListResponseEntity<HealthActivityAPIEntity>();
		response.setContent(list, more, null, null == json ? null : json.toString());
		return response;
	}

//	/**
//	 * 查出所有当前人参与的所有活动
//	 * @param registerId
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(value = "/activities/users", method = RequestMethod.GET,headers = {"version=2.[234].*"})
//	@ResponseBody
//	public JsonResponseEntity<List<HealthActivityAPIEntity>> getHealthActivityOfUser(
//			@RequestParam(value = "uid", required = true) String registerId,
//			@RequestHeader(value="screen-width")String width,
//			@RequestHeader(value="screen-height")String height,HttpServletRequest request) {
//		
//			JsonResponseEntity<List<HealthActivityAPIEntity>> response = new JsonResponseEntity<List<HealthActivityAPIEntity>>();
//			List<HealthActivityAPIEntity> list = new ArrayList<HealthActivityAPIEntity>();
//			
//			List<HealthActivityDetail> detailList = healthActivityDetailRepository.findActivitiesByRegisterId(registerId);
//			for (HealthActivityDetail detail : detailList) {
//				HealthActivityInfo info = detail.getHealthActivityInfo();
//				
//				HealthActivityAPIEntity entity = new HealthActivityAPIEntity(info, detail ,"activityMine",width,height);
//				this.setDetailInfo(entity,info,registerId);
//				list.add(entity);
//			}
//			response.setData(list);
//			return response;
//
//	}
//
//	/**
//	 * 查询活动信息
//	 * @return
//	 */
//	@RequestMapping(value = "/activities/detail", method = RequestMethod.GET,headers = {"version=2.[234].*"})
//	@ResponseBody
//	public JsonResponseEntity<HealthActivityAPIEntity> getHealthActivityDetail(
//			@RequestParam(value="uid",required = false) String registerId,
//			@RequestParam(value = "activityid", required = true)  String activityid,
//			@RequestHeader(value="screen-width")String width,
//			@RequestHeader(value="screen-height")String height,
//			HttpServletRequest request) {
//
//			JsonResponseEntity<HealthActivityAPIEntity> response = new JsonResponseEntity<HealthActivityAPIEntity>();
//
//			HealthActivityInfo info = haiService.getHealthActivityInfo(activityid);
//			HealthActivityDetail detail = healthActivityDetailRepository.findActivityDetailByAidAndRid(activityid, registerId);
//			
//			if (null != info) {
//				HealthActivityAPIEntity entity = new HealthActivityAPIEntity(info , detail ,"activityDetail",width,height);
//				this.setDetailInfo(entity,info,registerId);
//				entity.setDescription(null);
//
//				String basePath = request.getScheme() + "://"
//						+ request.getServerName() + ":" + request.getServerPort()
//						+ request.getContextPath() + "/api/";
//				entity.setContentUrl(basePath+"activities/detail/content?activityid="+activityid+"&isToken=1");
//
//				response.setData(entity);
//			}
//			return response;
//	}
//
	private void setDetailInfo(HealthActivityAPIEntity entity,HealthActivityInfo info,String registerid){
//		entity.setTotalApplied(healthActivityDetailRepository.findActivityRegistrationByActivityId(info.getActivityid()));// 已报名人数
//		if(!StringUtils.isEmpty(registerid)) {
//			entity.setIsApplied(healthActivityDetailRepository.
//					findActivityDetailByActivityIdAndRegisterid(info.getActivityid(), registerid));// （按钮状态）0：未报名，1：已报名
//		}else{
//			entity.setIsApplied("0");
//		}

//		entity.setScore(scoreCache.getScore().get("doJoinAct"));
//		String province = StringUtils.isEmpty(info.getProvince())?"":dictCache.queryArea(info.getProvince());
//		String city = (province.contains("上海") || province.contains("北京") || province.contains("重庆") || province.contains("天津") ||
//				StringUtils.isEmpty(info.getCity()))?"":dictCache.queryArea(info.getCity());
//		String county = StringUtils.isEmpty(info.getCounty())?"":dictCache.queryArea(info.getCounty());
//		entity.setLocation(province+city+county+info.getLocate());
//		entity.setHost((StringUtils.isEmpty(city)?dictCache.queryArea(info.getCounty()):city)+info.getHost());

	}
//
//
//	@RequestMapping(value = "/activities/detail/content", method = RequestMethod.GET,headers = {"version=2.[234].*"})
//	public ModelAndView getActivityDetailContent(
//			@RequestParam(value="activityid",required=true)String activityid){
//
//		HealthActivityInfo info = haiService.getHealthActivityInfo(activityid);
//
//		JsonResponseEntity<String> response = new JsonResponseEntity<String>();
//		response.setData(StringUtils.isEmpty(info.getSummary())?"":info.getSummary());
//		Gson gson = new Gson();
//		ModelAndView mav = new ModelAndView("activities/index", HttpClientParamFactory.generate("info", gson.toJson(response)));
//		return mav;
//	}
//
//	/**
//	 * 报名活动
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(value = "/activities/participation", method = RequestMethod.POST,headers = {"version=2.[234].*"})
//	@ResponseBody
//	public JsonResponseEntity<String> doParticipationActivity(
//			@RequestBody String request) {
//
//			JsonKeyReader reader = new JsonKeyReader(request);
//			String activityid =  reader.readString("activityid", false);
//			String registerId = reader.readString("uid", false);
//
//			JsonResponseEntity<String> response = new JsonResponseEntity<String>();
//			HealthActivityDetail detail = healthActivityDetailRepository.findActivityDetailByAidAndRid(activityid, registerId);
//			if (detail == null) {
//				HealthActivityInfo info = healthActivityRepository.findOne(activityid);
//				
//				String totalApply = healthActivityDetailRepository.findActivityRegistrationByActivityId(activityid);// 已报名人数
//				Integer quota = info.getQuota();// 活动限定名额
//
////				Date activityTime = info.getStarttime();
//				if (info.getEnroll_start_time().after(new Timestamp(System.currentTimeMillis()))) {
//					response.setCode(1620);
//					response.setMsg("活动报名尚未开始，报名失败！");
//					return response;
//				}else if (info.getEnroll_end_time().before(new Timestamp(System.currentTimeMillis()))) {
//					response.setCode(1608);
//					response.setMsg("活动报名结束，报名失败！");
//					return response;
//				}else if (totalApply != null && Integer.valueOf(totalApply) >= quota) {
//					response.setCode(1609);
//					response.setMsg("超过活动限定名额，报名失败！");
//					return response;
//
//				}else {
//					HealthActivityDetail detailInfo = new HealthActivityDetail();
//					detailInfo.setSigntime(DateFormatter.dateTimeFormat(new Date()));
//					detailInfo.setRegisterid(registerId);
//					detailInfo.setActivityid(activityid);
//					detailInfo.setId(IdGen.uuid());
//					healthActivityDetailRepository.save(detailInfo);
//					response.setCode(0);//1：报名成功
//					response.setMsg("报名成功");
//					
//					if(healthActivityDetailRepository.findActivityDetailByAidAndRidNum(activityid, registerId) > 1){
//						healthActivityDetailRepository.delete(detailInfo);
//						response.setCode(1610);
//						response.setMsg("不能重复报名，报名失败！");
//						return response;
//					}
//					if(Integer.parseInt(healthActivityDetailRepository.findActivityRegistrationByActivityId(activityid)) > quota){
//						healthActivityDetailRepository.delete(detailInfo);
//						response.setCode(1609);
//						response.setMsg("超过活动限定名额，报名失败！");
//						return response;
//					}
//				}
//			} else {
//				response.setCode(1610);
//				response.setMsg("不能重复报名，报名失败！");
//				return response;
//			}
//		response.setMsg("报名成功");
//			
//		return response;
//	}
//
//	/**
//	 * 取消活动报名
//	 * @param activityid
//	 * @return
//	 */
//	@RequestMapping(value = "/activities/participation", method = RequestMethod.DELETE,headers = {"version=2.[234].*"})
//	@ResponseBody
//	public JsonResponseEntity<String> doCancelParticipation(
//			@RequestParam(value="uid",required=true) String registerId,
//			@RequestParam(value = "activityid", required = true) String activityid) {
//
//
//			JsonResponseEntity<String> response = new JsonResponseEntity<String>();
//			HealthActivityDetail detail = healthActivityDetailRepository
//					.findActivityDetailByAidAndRid(activityid, registerId);
//			HealthActivityInfo info = detail.getHealthActivityInfo();
//			
//			Date activityTime = info.getStarttime();
//			if (info.getEndtime().before(new Timestamp(System.currentTimeMillis()))) {
//				response.setCode(1616);
//				response.setMsg("活动已结束不能取消报名！");
//				return response;
//			}
//			else if (activityTime.before(new Timestamp(System.currentTimeMillis()))) {
//				response.setCode(1611);
//				response.setMsg("活动已开始不能取消报名！");
//				return response;
//			} else {
//				detail.setDelFlag("1");
//				detail = healthActivityDetailRepository.save(detail);
//				int result = detail.getDelFlag().equals("1") ? 1 : 0;// 0:取消报名失败，1：取消报名成功
//				if(detail.getDelFlag().equals("1")){
//					response.setCode(0);
//					response.setMsg("取消报名成功");
//				}else{
//					response.setCode(1612);
//					response.setMsg("取消报名失败");
//				}
//				return response;
//			}
//	}
//
//	/**
//	 * 评价
//	 * @return
//	 */
//	@RequestMapping(value = "/activities/evaluation", method = RequestMethod.POST,headers = {"version=2.[234].*"})
//	@ResponseBody
//	public JsonResponseEntity<HealthActivityEvaluationAPIEntity> doEvaluationActivity(@RequestBody String request) {
//
//			JsonKeyReader reader = new JsonKeyReader(request);
//			String activityid =  reader.readString("activityid", false);
//			String evaluation =  reader.readString("evaluation", false);
//			String comment =  reader.readString("comment", false);
//			String registerId = reader.readString("uid", false);
//
//			JsonResponseEntity<HealthActivityEvaluationAPIEntity> response = new JsonResponseEntity<HealthActivityEvaluationAPIEntity>();
//			HealthActivityInfo info = healthActivityRepository.findOne(activityid);
//			if (info.getEndtime().after(new Timestamp(System.currentTimeMillis()))) {
//				response.setCode(1613);
//				response.setMsg("活动未结束不能进行评价！");
//				return response;
//			} else {
//				HealthActivityDetail detail = healthActivityDetailRepository.findActivityDetailByAidAndRid(activityid, registerId);
//				if (detail != null) {
//					detail.setEvaluate(evaluation);
//					detail.setEvalatememo(comment);
//					detail.setEvaluatetime(DateFormatter.dateTimeFormat(new Date()));
//					detail.setId(detail.getId());
//					int result = healthActivityDetailRepository.updateActivityDetailByAidAndRid(evaluation,
//									comment, DateFormatter.dateTimeFormat(new Date()),detail.getId());
//					if (result < 1) {
//						response.setCode(1614);
//						response.setMsg("评价失败！");
//						return response;
//					}
//				}
//			}
//
//		response.setMsg("评价成功");
//		return response;
//	}
//
//	/**
//	 * 满意度调查评价
//	 * @return
//	 */
//	@RequestMapping(value = "/activities/survey", method = RequestMethod.POST,headers = {"version=2.[234].*"})
//	@ResponseBody
//	public JsonResponseEntity<HealthActivityAPIEntity> doActivitySurvey(@RequestBody String request) {
//
//		JsonKeyReader reader = new JsonKeyReader(request);
//		String activityid =  reader.readString("activityid", false);
//		String content =  reader.readString("content", false);
//		String registerId = reader.readString("uid", false);
//
//		JsonResponseEntity<HealthActivityAPIEntity> response = new JsonResponseEntity<HealthActivityAPIEntity>();
//		HealthActivityDetail detail = healthActivityDetailRepository
//				.findActivityDetailByAidAndRid(activityid, registerId);
//		HealthActivityInfo info = detail.getHealthActivityInfo();
//		
//		if (detail.getEvaluatetime() != null) {
//			detail.setInvesttime(DateFormatter.dateTimeFormat(new Date()));
//			detail.setInvestcontent(content);
//			detail = healthActivityDetailRepository.save(detail);
//			if(!detail.getInvesttime().equals("")){
//				response.setCode(0);
//				response.setMsg("满意度调查成功");
//			}else{
//				response.setCode(1615);
//				response.setMsg("满意度调查失败");
//			}
//
//		} else {// 如果评价时间为空
//			HealthActivityAPIEntity entity = new HealthActivityAPIEntity();
//			entity.setEvaluation(null);
//			response.setData(entity);
//			response.setCode(0);
//		}
//			
//		return response;
//	}
	

}

