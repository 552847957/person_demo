package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/16.
 */
@XmlRootElement(name = "Result")
public class SegmentNumberInfo {

    private String startTime;
    private String endTime;

    /**
     * 序号
     * 以|分割的数字串，选择一个传给预约提交时的visitNo??
     */
    private String number;
    private String numSourceId;
    /**
     * 最新的剩余号源数
     *
     */
    private String reserveOrderNum;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumSourceId() {
        return numSourceId;
    }

    public void setNumSourceId(String numSourceId) {
        this.numSourceId = numSourceId;
    }

    public String getReserveOrderNum() {
        return reserveOrderNum;
    }

    public void setReserveOrderNum(String reserveOrderNum) {
        this.reserveOrderNum = reserveOrderNum;
    }
}
