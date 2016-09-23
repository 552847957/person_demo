package com.wondersgroup.healthcloud.api.http.dto.doctor.interven;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by zhuchunliu on 2015/9/8.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterventionDetailAPIEntity {
    private String name;
    private String ids;
    private List<Event> eventList ;

    public InterventionDetailAPIEntity(){

    }
    public InterventionDetailAPIEntity(String ids,String name,List<Event> eventList){
        this.ids = ids;
        this.name = name;
        this.eventList = eventList;
    }
    public class Event{
        private String eventName;//异常名称
        private String eventPrompt;//异常提示
        private List<EventDetail> eventDetailList ;
        public Event(String eventName,String eventPrompt,List<EventDetail> eventDetailList){
            this.eventName = eventName;
            this.eventPrompt = eventPrompt;
            this.eventDetailList = eventDetailList;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getEventPrompt() {
            return eventPrompt;
        }

        public void setEventPrompt(String eventPrompt) {
            this.eventPrompt = eventPrompt;
        }

        public List<EventDetail> getEventDetailList() {
            return eventDetailList;
        }

        public void setEventDetailList(List<EventDetail> eventDetailList) {
            this.eventDetailList = eventDetailList;
        }
    }

    public class EventDetail{
        private String index;//时间标号
        private String endDate;//干预截止日期
        private List<Detail> detailList;
        public EventDetail(String index,String endDate,List<Detail> detailList){
            this.index = index;
            this.endDate = endDate;
            this.detailList = detailList;
        }
        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public List<Detail> getDetailList() {
            return detailList;
        }

        public void setDetailList(List<Detail> detailList) {
            this.detailList = detailList;
        }
    }

    public class Detail{
        private String date;//测量日期
        private String result;//测量结果
        public Detail(String date,String result){
            this.date = date;
            this.result = result;
        }
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}
