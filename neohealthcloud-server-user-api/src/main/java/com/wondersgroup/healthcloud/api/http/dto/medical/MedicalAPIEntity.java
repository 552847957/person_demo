package com.wondersgroup.healthcloud.api.http.dto.medical;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by dukuanxin on 16/5/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicalAPIEntity {
	private String id;// 服务id
	private String fwnrName;//服务名称
	private Object grFee;//个人支付
	private Object ybFee;//医保支付
	private String memo;//服务内容
	private String smallimg;//列表图片
	private String bigimg;//详情图片
	private Object dj1;//单价
	private String isneedphoto;//是否需要附件
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFwnrName() {
		return fwnrName;
	}
	public void setFwnrName(String fwnrName) {
		this.fwnrName = fwnrName;
	}
	public Object getGrFee() {
		return grFee;
	}
	public void setGrFee(Object grFee) {
		this.grFee = grFee;
	}
	public Object getYbFee() {
		return ybFee;
	}
	public void setYbFee(Object ybFee) {
		this.ybFee = ybFee;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getSmallimg() {
		return smallimg;
	}
	public void setSmallimg(String smallimg) {
		this.smallimg = smallimg;
	}
	public String getBigimg() {
		return bigimg;
	}
	public void setBigimg(String bigimg) {
		this.bigimg = bigimg;
	}
	public Object getDj1() {
		return dj1;
	}
	public void setDj1(Object dj1) {
		this.dj1 = dj1;
	}
	public String getIsneedphoto() {
		return isneedphoto;
	}
	public void setIsneedphoto(String isneedphoto) {
		this.isneedphoto = isneedphoto;
	}
}
