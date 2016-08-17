package com.wondersgroup.healthcloud.services.user.dto.healthactivity;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityDetail;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;
import com.wondersgroup.healthcloud.utils.DateFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HealthActivityAPIEntity {
	private String id;//活动id
	private String host;// 主办方
	private String name; // '主题',
	private String time;// '开始时间',
	private String location;// '举办地点',
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
	private String overdue;//活动是否过期：0为未过期，1为过期
	private String enrollOverdue;//报名时间是否过期：0为未过期，1为过期
	private boolean ltDay; //
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
		
		String startMonDay = monthDay_sdf.format(info.getStarttime());
		String endMonDay = monthDay_sdf.format(info.getEndtime());
		String startHourMin = hourMinute_sdf.format(info.getStarttime());
		String endHourMin = hourMinute_sdf.format(info.getEndtime());
		
		this.overdue = info.getEndtime().getTime() < new Date().getTime() ? "1" : "0";
		this.enrollOverdue = info.getEnrollStartTime().getTime() < new Date().getTime() ? "1" : "0";
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
	}

	
	
}
