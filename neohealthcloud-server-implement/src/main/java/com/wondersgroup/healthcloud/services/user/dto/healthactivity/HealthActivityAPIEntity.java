package com.wondersgroup.healthcloud.services.user.dto.healthactivity;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityDetail;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;
import com.wondersgroup.healthcloud.utils.DateFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthActivityAPIEntity {
	private String id;//活动id
	private String host;// 主办方
	private String name; // '主题',
	private String time;// '开始时间',
	private String location;// '举办地点',
	private Integer totalAvailable;// '名额',
	private Integer totalApplied;// 报名人数
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
	private String overdue;//活动是否过期：0为未过期，1为过期
	private String enrollOverdue;//报名时间是否过期：0为未过期，1为过期
	private boolean ltDay; //再报名时间未过期的情况下，报名剩余时间是否只剩一天
	private String enrollCountdown;//报名倒计时
	
	private HealthActivityEvaluationAPIEntity evaluation;
	private SimpleDateFormat monthDay_sdf = new SimpleDateFormat("MM.dd");
	private SimpleDateFormat hourMinute_sdf = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat time_adf = new SimpleDateFormat("yyyy-MM-dd MM:ss:dd");
	
	private String              starttime;                               // '开始时间',
    private String              endtime;                                 // '结束时间',
    private String              onlineTime;                              //上线时间
    private String              offlineTime;                             //下线时间
    private String              enrollStartTime;                         //活动报名时间'
    private String              enrollEndTime;                           //活动结束时间
	
  
	public HealthActivityAPIEntity(HealthActivityInfo info,String width,String height){
		init(info  ,"activityList",width,height);
	}
	
	public HealthActivityAPIEntity(HealthActivityInfo info , HealthActivityDetail detail , String pageType,String width,String height){
		init(info  ,pageType,width,height);
	}
	
	public void init(HealthActivityInfo info,String pageType,String width,String height){
		Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.id = info.getActivityid();
		this.host = info.getHost();
		this.name = info.getTitle();
		
		String startMonDay = monthDay_sdf.format(info.getStarttime());
		String endMonDay = monthDay_sdf.format(info.getEndtime());
		String startHourMin = hourMinute_sdf.format(info.getStarttime());
		String endHourMin = hourMinute_sdf.format(info.getEndtime());
		
		this.overdue = info.getEndtime().getTime() < new Date().getTime() ? "1" : "0";
		if("0".equals(this.overdue)){
		    this.enrollOverdue = info.getEnrollEndTime().getTime() < new Date().getTime() ? "1" : "0";
		}
		if("0".equals(this.enrollOverdue)){
		    this.ltDay = (info.getEndtime().getTime() - new Date().getTime()) < 86400000;
		}
		if("activityMine".equals(pageType)){//我参与的活动
			this.time= DateFormatter.yearFormat(info.getStarttime())
					+ "/"+ startMonDay+(startMonDay.equals(endMonDay)?"":"～" + endMonDay)
					+ "/"+startHourMin+(startHourMin.equals(endHourMin)?"":"--"+endHourMin);
		}else{//活动列表、活动详情
			this.time= startMonDay+(startMonDay.equals(endMonDay)?"":"～" + endMonDay)
					+ " "+startHourMin+(startHourMin.equals(endHourMin)?"":"--"+endHourMin);
		} 
		this.totalAvailable = info.getQuota();

		
		this.speechMaker = info.getSpeaker();
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
		if(info.getStarttime() != null){ 
		    this.starttime = time_adf.format(info.getStarttime());
		}
		if(info.getEndtime() != null){ 
		    this.endtime = time_adf.format(info.getEndtime());
        }
		if(info.getOnlineTime() != null){ 
		    this.onlineTime = time_adf.format(info.getOnlineTime());
        }
		if(info.getOfflineTime() != null){ 
		    this.offlineTime = time_adf.format(info.getOfflineTime());
        }
		if(info.getEnrollStartTime() != null){ 
		    this.enrollStartTime = time_adf.format(info.getEnrollStartTime());
        }
		if(info.getEnrollEndTime() != null){ 
		    this.enrollEndTime = time_adf.format(info.getEnrollEndTime());
        }
		if("0".equals(enrollOverdue)){
	        this.enrollCountdown = getDateTimeStr(info);
		}
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(Integer totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public Integer getTotalApplied() {
        return totalApplied;
    }

    public void setTotalApplied(Integer totalApplied) {
        this.totalApplied = totalApplied;
    }

    public String getIsApplied() {
        return isApplied;
    }

    public void setIsApplied(String isApplied) {
        this.isApplied = isApplied;
    }

    public String getSpeechMaker() {
        return speechMaker;
    }

    public void setSpeechMaker(String speechMaker) {
        this.speechMaker = speechMaker;
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

    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getIsEvaluation() {
        return isEvaluation;
    }

    public void setIsEvaluation(String isEvaluation) {
        this.isEvaluation = isEvaluation;
    }

    public String getIsSurvey() {
        return isSurvey;
    }

    public void setIsSurvey(String isSurvey) {
        this.isSurvey = isSurvey;
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

    public String getEnrollOverdue() {
        return enrollOverdue;
    }

    public void setEnrollOverdue(String enrollOverdue) {
        this.enrollOverdue = enrollOverdue;
    }

    public boolean isLtDay() {
        return ltDay;
    }

    public void setLtDay(boolean ltDay) {
        this.ltDay = ltDay;
    }

    public HealthActivityEvaluationAPIEntity getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(HealthActivityEvaluationAPIEntity evaluation) {
        this.evaluation = evaluation;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    public String getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(String offlineTime) {
        this.offlineTime = offlineTime;
    }

    public String getEnrollStartTime() {
        return enrollStartTime;
    }

    public void setEnrollStartTime(String enrollStartTime) {
        this.enrollStartTime = enrollStartTime;
    }

    public String getEnrollEndTime() {
        return enrollEndTime;
    }

    public void setEnrollEndTime(String enrollEndTime) {
        this.enrollEndTime = enrollEndTime;
    }

    public String getEnrollCountdown() {
        return enrollCountdown;
    }

    public void setEnrollCountdown(String enrollCountdown) {
        this.enrollCountdown = enrollCountdown;
    }

    public String getDateTimeStr(HealthActivityInfo info){
        long quot = 0;
        quot = info.getEnrollEndTime().getTime() - info.getEnrollStartTime().getTime();
        quot = quot / 1000 / 60;
        quot = quot < 0 ? quot * -1 : quot;

        String str = "";
        int a = 2000;
        int dat = a/(60*24);
        a = a - (dat * (60 * 24));
        str = str + (String.valueOf(dat).length() > 1 ? dat : "0" + dat) + ":";
        
        int hour = a/(60);
        
        a = a - (hour * 60);
        str = str + (String.valueOf(hour).length() > 1 ? hour : "0" + hour) + ":";
        str = str + (String.valueOf(a).length() > 1 ? a : "0" + a);
        return str;
    }
}
