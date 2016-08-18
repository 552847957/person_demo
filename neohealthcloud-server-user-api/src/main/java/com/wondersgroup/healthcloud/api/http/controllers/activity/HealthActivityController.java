package com.wondersgroup.healthcloud.api.http.controllers.activity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityDetail;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;
import com.wondersgroup.healthcloud.jpa.repository.activiti.HealthActivityDetailRepository;
import com.wondersgroup.healthcloud.jpa.repository.activiti.HealthActivityInfoRepository;
import com.wondersgroup.healthcloud.services.user.HealthActivityInfoService;
import com.wondersgroup.healthcloud.services.user.dto.healthactivity.HealthActivityAPIEntity;
import com.wondersgroup.healthcloud.utils.DateFormatter;

@RestController
@RequestMapping("/api")
public class HealthActivityController {
    private static int pageSize = 10;
    
	@Autowired
	private HealthActivityInfoService haiService;

	@Autowired
    private DictCache dictCache; 

	@Autowired
	private HealthActivityInfoRepository healthActivityRepository;

	@Autowired
	private HealthActivityDetailRepository healthActivityDetailRepository;

	/**
	 * 根据类型和区域查询健康活动
	 * @param registerid
	 * @param area
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/activities", method = RequestMethod.GET)
	@VersionRange
	public JsonResponseEntity<List<HealthActivityAPIEntity>> getHealthActivityList(
			@RequestParam(value = "uid",required = false) String registerid,
			@RequestParam(value = "location", required = true) String area,
			@RequestParam(value = "type", required = true) String type,
			@RequestHeader(value="screen-width")String width,
			@RequestHeader(value="screen-height")String height,HttpServletRequest request) {

			JsonResponseEntity<List<HealthActivityAPIEntity>> response = new JsonResponseEntity<List<HealthActivityAPIEntity>>();
			List<HealthActivityAPIEntity> list = new ArrayList<HealthActivityAPIEntity>();
			
			List<HealthActivityInfo> infoList = haiService.getHealthActivityInfos(area, type);
			for (HealthActivityInfo info : infoList) {
				
				HealthActivityAPIEntity entity = new HealthActivityAPIEntity(info,width,height);
				this.setDetailInfo(entity,info,registerid);
				list.add(entity);
			}
			response.setData(list);
			return response;
	}

	/**
	 * 根据类型和区域查询健康活动【分页功能】
	 * @param uid
	 * @param location
	 * @return
	 */
	@RequestMapping(value = "/activities/page", method = RequestMethod.GET)
	@VersionRange
	public JsonListResponseEntity<HealthActivityAPIEntity> getHealthActivityPageList(
			@RequestParam(value = "uid",required = false) String registerid,
			@RequestParam(value = "province", required = true) String province,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "county", required = false) String county,
			@RequestParam(value = "status", defaultValue = "1") Integer status,
			@RequestParam(value = "flag", defaultValue = "1") Integer flag,
			@RequestHeader(value="screen-width")String width,
			@RequestHeader(value="screen-height")String height,HttpServletRequest request) {

	    
		List<HealthActivityInfo> infoList = haiService.getHealthActivityInfos(province, city, county, status, flag, pageSize);
		Boolean more = false;
		if(!infoList.isEmpty() && infoList.size() == 10){
		    List<HealthActivityInfo> list = haiService.getHealthActivityInfos(province, city, county, status, flag + 1, pageSize);
		    more = !list.isEmpty();
		}
		List<HealthActivityAPIEntity> list = new ArrayList<HealthActivityAPIEntity>();
		for (HealthActivityInfo info : infoList) {
			HealthActivityAPIEntity entity = new HealthActivityAPIEntity(info,width,height);
			this.setDetailInfo(entity,info,registerid);
			list.add(entity);
		}

		JsonListResponseEntity<HealthActivityAPIEntity> response = new JsonListResponseEntity<HealthActivityAPIEntity>();
		response.setContent(list, more, null, String.valueOf((flag + 1)));
		return response;
	}

	/**
	 * 查出所有当前人参与的所有活动
	 * @param registerId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/activities/users", method = RequestMethod.GET)
	@VersionRange
	public JsonResponseEntity<List<HealthActivityAPIEntity>> getHealthActivityOfUser(
			@RequestParam(value = "uid", required = true) String registerId,
			@RequestHeader(value="screen-width")String width,
			@RequestHeader(value="screen-height")String height,HttpServletRequest request) {
		
			JsonResponseEntity<List<HealthActivityAPIEntity>> response = new JsonResponseEntity<List<HealthActivityAPIEntity>>();
			List<HealthActivityAPIEntity> list = new ArrayList<HealthActivityAPIEntity>();
			
			List<HealthActivityDetail> detailList = healthActivityDetailRepository.findActivitiesByRegisterId(registerId);
			for (HealthActivityDetail detail : detailList) {
				HealthActivityInfo info =  healthActivityRepository.findOne(detail.getActivityid());
				
				HealthActivityAPIEntity entity = new HealthActivityAPIEntity(info, detail ,"activityMine",width,height);
				this.setDetailInfo(entity,info,registerId);
				list.add(entity);
			}
			response.setData(list);
			return response;

	}

	/**
	 * 查询活动信息
	 * @return
	 */
	@RequestMapping(value = "/activities/detail", method = RequestMethod.GET)
	@VersionRange
	public JsonResponseEntity<HealthActivityAPIEntity> getHealthActivityDetail(
			@RequestParam(value="uid",required = false) String registerId,
			@RequestParam(value = "activityid", required = true)  String activityid,
			@RequestHeader(value="screen-width",required = false)String width,
			@RequestHeader(value="screen-height",required = false)String height,
			HttpServletRequest request) {
	        
			JsonResponseEntity<HealthActivityAPIEntity> response = new JsonResponseEntity<HealthActivityAPIEntity>();

			HealthActivityInfo info = haiService.getHealthActivityInfo(activityid);
			HealthActivityDetail detail = healthActivityDetailRepository.findActivityDetailByAidAndRid(activityid, registerId);
			
			if (null != info) {
				HealthActivityAPIEntity entity = new HealthActivityAPIEntity(info , detail ,"activityDetail",width,height);
				this.setDetailInfo(entity,info,registerId);

				response.setData(entity);
			}
			return response;
	}

	private void setDetailInfo(HealthActivityAPIEntity entity,HealthActivityInfo info,String registerid){
		entity.setTotalApplied(healthActivityDetailRepository.findActivityRegistrationByActivityId(info.getActivityid()));// 已报名人数
		if(!StringUtils.isEmpty(registerid)) {
			entity.setIsApplied(healthActivityDetailRepository.
					findActivityDetailByActivityIdAndRegisterid(info.getActivityid(), registerid));// （按钮状态）0：未报名，1：已报名
		}else{
			entity.setIsApplied("0");
		}
		
		String province = StringUtils.isEmpty(info.getProvince())?"":dictCache.queryArea(info.getProvince());
		String city = (province.contains("上海") || province.contains("北京") || province.contains("重庆") || province.contains("天津") ||
				StringUtils.isEmpty(info.getCity()))?"":dictCache.queryArea(info.getCity());
		String county = StringUtils.isEmpty(info.getCounty())?"":dictCache.queryArea(info.getCounty());
		entity.setLocation(province+city+county+info.getLocate());
		entity.setHost((StringUtils.isEmpty(city)?dictCache.queryArea(info.getCounty()):city)+info.getHost());

	}

	/**
	 * 报名活动
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/activities/participation", method = RequestMethod.POST)
	@VersionRange
	public JsonResponseEntity<String> doParticipationActivity(@RequestBody String request) {
	        
			JsonKeyReader reader = new JsonKeyReader(request);
			String activityid =  reader.readString("activityid", false);
			String registerId = reader.readString("uid", false);

			JsonResponseEntity<String> response = new JsonResponseEntity<String>();
			HealthActivityDetail detail = healthActivityDetailRepository.findActivityDetailByAidAndRid(activityid, registerId);
			if (detail == null) {
				HealthActivityInfo info = healthActivityRepository.findOne(activityid);
				
				String totalApply = healthActivityDetailRepository.findActivityRegistrationByActivityId(activityid);// 已报名人数
				Integer quota = info.getQuota();// 活动限定名额

				if (info.getEnrollStartTime().after(new Timestamp(System.currentTimeMillis()))) {
					response.setCode(1620);
					response.setMsg("活动报名尚未开始，报名失败！");
					return response;
				}else if (info.getEnrollEndTime().before(new Timestamp(System.currentTimeMillis()))) {
					response.setCode(1608);
					response.setMsg("活动报名结束，报名失败！");
					return response;
				}else if (totalApply != null && Integer.valueOf(totalApply) >= quota) {
					response.setCode(1609);
					response.setMsg("超过活动限定名额，报名失败！");
					return response;

				}else {
					HealthActivityDetail detailInfo = new HealthActivityDetail();
					detailInfo.setSigntime(DateFormatter.dateTimeFormat(new Date()));
					detailInfo.setRegisterid(registerId);
					detailInfo.setActivityid(activityid);
					detailInfo.setId(IdGen.uuid());
					detailInfo.setDelFlag("0");
					healthActivityDetailRepository.save(detailInfo);
					response.setCode(0);//1：报名成功
					response.setMsg("报名成功");
					
					if(healthActivityDetailRepository.findActivityDetailByAidAndRidNum(activityid, registerId) > 1){
						healthActivityDetailRepository.delete(detailInfo);
						response.setCode(1610);
						response.setMsg("不能重复报名，报名失败！");
						return response;
					}
					if(Integer.parseInt(healthActivityDetailRepository.findActivityRegistrationByActivityId(activityid)) > quota){
						healthActivityDetailRepository.delete(detailInfo);
						response.setCode(1609);
						response.setMsg("超过活动限定名额，报名失败！");
						return response;
					}
				}
			} else {
				response.setCode(1610);
				response.setMsg("不能重复报名，报名失败！");
				return response;
			}
		response.setMsg("报名成功");
			
		return response;
	}

	/**
	 * 取消活动报名
	 * @param activityid
	 * @return
	 */
	@RequestMapping(value = "/activities/participation", method = RequestMethod.DELETE)
	@VersionRange
	public JsonResponseEntity<String> doCancelParticipation(
			@RequestParam(value="uid",required=true) String registerId,
			@RequestParam(value = "activityid", required = true) String activityid) {


			JsonResponseEntity<String> response = new JsonResponseEntity<String>();
			HealthActivityDetail detail = healthActivityDetailRepository
					.findActivityDetailByAidAndRid(activityid, registerId);
			HealthActivityInfo info = healthActivityRepository.findOne(activityid);
			
			Date activityTime = info.getStarttime();
			if (info.getEndtime().before(new Timestamp(System.currentTimeMillis()))) {
				response.setCode(1616);
				response.setMsg("活动已结束不能取消报名！");
				return response;
			}
			else if (activityTime.before(new Timestamp(System.currentTimeMillis()))) {
				response.setCode(1611);
				response.setMsg("活动已开始不能取消报名！");
				return response;
			} else {
				detail.setDelFlag("1");
				detail = healthActivityDetailRepository.save(detail);
				int result = detail.getDelFlag().equals("1") ? 1 : 0;// 0:取消报名失败，1：取消报名成功
				if(detail.getDelFlag().equals("1")){
					response.setCode(0);
					response.setMsg("取消报名成功");
				}else{
					response.setCode(1612);
					response.setMsg("取消报名失败");
				}
				return response;
			}
	}

}

