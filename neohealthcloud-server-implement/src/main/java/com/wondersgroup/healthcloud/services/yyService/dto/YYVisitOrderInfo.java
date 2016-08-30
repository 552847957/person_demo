package com.wondersgroup.healthcloud.services.yyService.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 医养结合 上门服务 工单列表详情
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YYVisitOrderInfo {
    private String id;//工单编号workOrderNo
    private String status;//工单状态 0：待接单，1：待派单，2：待服务，3：待评价，4：已完成，9：取消

    /*private String memo;//评价内容
    private String commenttime;//评价时间
    private String xj;//评价星数
    private String isComment;//1：已评价，0：待评价*/

//    private String create_time;//下单时间
    private String elderid;//客户ID

    private String fwnrid;//服务id
    private String fwnrname;//工单护理类型：上门服务
    private String smallimg;//服务图标

    //    private String lxr;//联系人contact
    private String xm;//客户名称
    private String lxrlxdh;// 联系人的服务电话
    private String fwaddress;//联系人的服务地址

    private String endtime;//服务结束时间

    private String gr_fee;//个人支付
    private String yb_fee;//医保支付
    private String ispay;//1：已支付，0：未支付


    private String sendtime;//预约时间(创建时间工单)

    private List<OrderPhotos> tOrderPhotos;


    private String begintime;//签到时间 是否签到判断这个字段

    private String realyysjd;//实际服务时间段 上午/下午
    private String realyytime;//实际服务时间

//    private String yysjd;//服务时间段 上午/下午
//    private String yytime;//服务时间

    //服务列表展示的时间，服务未完成取sendtime，服务完成取realyytime
    private String service_showtime;//服务显示的时间 我们转换的时间，格式：2016-05-10 周一 下午

    private Integer sign_distance = 1000;//签到距离,默认1公里才能签到，单位m

    public class OrderPhotos{
        public String photoaddress;
        public String photoname;

        public OrderPhotos(){};
        public OrderPhotos(String address, String name){
            this.photoaddress = address;
            this.photoname = name;
        }
        public String getPhotoaddress() {
            return photoaddress;
        }

        public void setPhotoaddress(String photoaddress) {
            this.photoaddress = photoaddress;
        }

        public String getPhotoname() {
            return photoname;
        }

        public void setPhotoname(String photoname) {
            this.photoname = photoname;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getElderid() {
        return elderid;
    }

    public void setElderid(String elderid) {
        this.elderid = elderid;
    }

    public String getFwnrid() {
        return fwnrid;
    }

    public void setFwnrid(String fwnrid) {
        this.fwnrid = fwnrid;
    }

    public String getFwnrname() {
        return fwnrname;
    }

    public void setFwnrname(String fwnrname) {
        this.fwnrname = fwnrname;
    }

    public String getSmallimg() {
        return smallimg;
    }

    public void setSmallimg(String smallimg) {
        this.smallimg = smallimg;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getLxrlxdh() {
        return lxrlxdh;
    }

    public void setLxrlxdh(String lxrlxdh) {
        this.lxrlxdh = lxrlxdh;
    }

    public String getFwaddress() {
        return fwaddress;
    }

    public void setFwaddress(String fwaddress) {
        this.fwaddress = fwaddress;
    }

    public String getGr_fee() {
        return gr_fee;
    }

    public void setGr_fee(String gr_fee) {
        this.gr_fee = gr_fee;
    }

    public String getYb_fee() {
        return yb_fee;
    }

    public void setYb_fee(String yb_fee) {
        this.yb_fee = yb_fee;
    }

    public String getIspay() {
        return ispay;
    }

    public void setIspay(String ispay) {
        this.ispay = ispay;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public List<OrderPhotos> gettOrderPhotos() {
        return tOrderPhotos;
    }

    public void addtOrderPhotos(OrderPhotos orderPhotos) {
        if (this.tOrderPhotos == null){
            this.tOrderPhotos = new ArrayList<>();
        }
        this.tOrderPhotos.add(orderPhotos);
    }

    public void addtOrderPhotos(String address, String name) {
        if (this.tOrderPhotos == null){
            this.tOrderPhotos = new ArrayList<>();
        }
        this.tOrderPhotos.add(new OrderPhotos(address, name));
    }

    public void settOrderPhotos(List<OrderPhotos> tOrderPhotos) {
        this.tOrderPhotos = tOrderPhotos;
    }

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }


    public String getService_showtime() {
        return service_showtime;
    }

    public void setService_showtime(String service_showtime) {
        this.service_showtime = service_showtime;
    }

    public String getRealyysjd() {
        return realyysjd;
    }

    public void setRealyysjd(String realyysjd) {
        this.realyysjd = realyysjd;
    }

    public String getRealyytime() {
        return realyytime;
    }

    public void setRealyytime(String realyytime) {
        this.realyytime = realyytime;
    }

    public Integer getSign_distance() {
        return sign_distance;
    }

    public void setSign_distance(Integer sign_distance) {
        this.sign_distance = sign_distance;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}
