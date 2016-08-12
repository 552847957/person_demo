package com.wondersgroup.healthcloud.services.user.dto.healthactivity;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.healthcloud.jpa.entity.activiti.HealthActivityDetail;
import com.wondersgroup.healthcloud.jpa.entity.activiti.HealthActivityInfo;
import com.wondersgroup.healthcloud.utils.DateFormatter;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthActivityAPIEntity {
	private String id;//活动id
	private String host;// 主办方
	private String name; // '主题',
	private String time;// '开始时间',
	private String location;// '举办地点',
	private String module; // '活动类型 1：糖尿病:2：高血压',
	
	private Integer totalAvailable;// '名额',
	private String totalApplied;// 报名人数
	private String isApplied; // 是否已报名（按钮状态）
	
	private String speechMaker;// '主讲人信息 姓名 科室 职务',
	private String department;//科室
	private String pftitle;//职称

	private Integer style;// 讲座形式
	private String description;// '活动概述',
	
	private String picture; // '活动图片存入attach表',
	private String thumbnail;//活动缩略图
	
	private Double score;// 积分
	private String isEvaluation;//是否可以评价 0:不能评价，1：能评价
	private String isSurvey;//是否做过满意度调查 0:未做调查，1：已做调查
	private String contentUrl;//活动内容url地址
	private String overdue;//是否过期：0为未过期，1为过期
	
	private HealthActivityEvaluationAPIEntity evaluation;
	private SimpleDateFormat monthDay_sdf = new SimpleDateFormat("MM.dd");
	private SimpleDateFormat hourMinute_sdf = new SimpleDateFormat("HH:mm");
	
	private DateFormatter df = new DateFormatter();
	
	
	public HealthActivityAPIEntity(){
		
	}
	public HealthActivityAPIEntity(HealthActivityInfo info,String width,String height){
		init(info  ,"activityList",width,height);
	}
	
	public HealthActivityAPIEntity(HealthActivityInfo info , HealthActivityDetail detail , String pageType,String width,String height){
		
		init(info  ,pageType,width,height);
		
		Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		
		if (null != detail) {

			this.evaluation = new HealthActivityEvaluationAPIEntity(detail);

			if (info.getStarttime().before(nowTime) && info.getEndtime().before(nowTime) && null == detail.getEvaluatetime()) {
				this.isEvaluation = "1";//能评价
				this.evaluation = null;
			}else{
				this.isEvaluation = "0";//不能评价
			}
			
			if (null != detail.getEvaluatetime() && detail.getInvesttime() != null) {
				this.isSurvey = "1";// 是否做过满意度调查 0:未做调查，1：已做调查
			} else {
				this.isSurvey = "0";
			}
		}else{
			this.isEvaluation = "0";
			this.isSurvey = "0";
		}
	}
	
	public void init(HealthActivityInfo info,String pageType,String width,String height){
		Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.id = info.getActivityid();
		this.host = info.getHost();
		this.name = info.getTitle();
		Date stratTime = info.getStarttime();
		Date endTime = info.getEndtime();
		
		String startMonDay = monthDay_sdf.format(info.getStarttime());
		String endMonDay = monthDay_sdf.format(info.getEndtime());
		String startHourMin = hourMinute_sdf.format(info.getStarttime());
		String endHourMin = hourMinute_sdf.format(info.getEndtime());
		if(info.getEndtime().getTime()<new Date().getTime()){
			this.overdue = "1";
		}else{
			this.overdue = "0";
		}
		if("activityMine".equals(pageType)){//我参与的活动
			this.time= DateFormatter.yearFormat(info.getStarttime())
					+ "/"+ startMonDay+(startMonDay.equals(endMonDay)?"":"～" + endMonDay)
					+ "/"+startHourMin+(startHourMin.equals(endHourMin)?"":"--"+endHourMin);
		}else{//活动列表、活动详情
			this.time= startMonDay+(startMonDay.equals(endMonDay)?"":"～" + endMonDay)
					+ " "+startHourMin+(startHourMin.equals(endHourMin)?"":"--"+endHourMin);
		} 
		this.module = info.getType();
		this.totalAvailable = info.getQuota();

		
		this.speechMaker = info.getSpeaker()+ (StringUtils.isEmpty(info.getDepartment())?"":" "+info.getDepartment())+
				(StringUtils.isEmpty(info.getPftitle())?"":" "+info.getPftitle());
		this.style = info.getStyle();
		this.description = info.getSummary();
		this.picture = info.getPhoto();
		this.thumbnail = info.getThumbnail()+ImagePath.thumbnailPostfix(Integer.parseInt(width)/3+"",Integer.parseInt(width)/3+"");


		if (info.getStarttime().before(nowTime)
				&& info.getEndtime().before(nowTime)) {
			this.isEvaluation = "1";// 是否可以评价 0:不能评价，1：能评价
		} else {
			this.isEvaluation = "0";
		}
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getIsSurvey() {
		return isSurvey;
	}

	public void setIsSurvey(String isSurvey) {
		this.isSurvey = isSurvey;
	}

	
	public String getIsEvaluation() {
		return isEvaluation;
	}

	public void setIsEvaluation(String isEvaluation) {
		this.isEvaluation = isEvaluation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public Integer getStyle() {
		return style;
	}

	public void setStyle(Integer style) {
		this.style = style;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public HealthActivityEvaluationAPIEntity getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(HealthActivityEvaluationAPIEntity evaluation) {
		this.evaluation = evaluation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getTotalAvailable() {
		return totalAvailable;
	}

	public void setTotalAvailable(Integer totalAvailable) {
		this.totalAvailable = totalAvailable;
	}

	public String getSpeechMaker() {
		return speechMaker;
	}

	public void setSpeechMaker(String speechMaker) {
		this.speechMaker = speechMaker;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getIsApplied() {
		return isApplied;
	}

	public void setIsApplied(String isApplied) {
		this.isApplied = isApplied;
	}

	public String getTotalApplied() {
		return totalApplied;
	}

	public void setTotalApplied(String totalApplied) {
		this.totalApplied = totalApplied;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPftitle() {
		return pftitle;
	}

	public void setPftitle(String pftitle) {
		this.pftitle = pftitle;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getOverdue() {
		return overdue;
	}

	public void setOverdue(String overdue) {
		this.overdue = overdue;
	}
}
