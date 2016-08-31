package com.wondersgroup.healthcloud.api.http.dto.medical;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by dukuanxin on 18/5/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceOrderAPIEntity {
	
	private String id;//订单编号
	private String fwnrname;//服务名称
	private String address;//服务地址
	private String status;//订单状态（0 待接单1 待派单2 待服务3 待评价4 已完成9 取消）
	private String smallimg;//服务图标
	private String ispay;//是否已付款	
	private Object orderList;//工单
	private String lxr;//联系人
	private String lxrlxdh;//联系人电话
	private Object totalfee;//服务费用
	private Object createTime;//创建时间
	private Object photoList;//附件
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFwnrname() {
		return fwnrname;
	}
	public void setFwnrname(String fwnrname) {
		this.fwnrname = fwnrname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSmallimg() {
		return smallimg;
	}
	public void setSmallimg(String smallimg) {
		this.smallimg = smallimg;
	}
	public String getIspay() {
		return ispay;
	}
	public void setIspay(String ispay) {
		this.ispay = ispay;
	}
	public Object getOrderList() {
		return orderList;
	}
	public void setOrderList(Object object) {
		this.orderList = object;
	}
	public String getLxr() {
		return lxr;
	}
	public void setLxr(String lxr) {
		this.lxr = lxr;
	}
	public String getLxrlxdh() {
		return lxrlxdh;
	}
	public void setLxrlxdh(String lxrlxdh) {
		this.lxrlxdh = lxrlxdh;
	}
	public Object getTotalfee() {
		return totalfee;
	}
	public void setTotalfee(Object totalfee) {
		this.totalfee = totalfee;
	}
	public Object getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Object createTime) {
		this.createTime = createTime;
	}
	public Object getPhotoList() {
		return photoList;
	}
	public void setPhotoList(Object photoList) {
		this.photoList = photoList;
	}
}
