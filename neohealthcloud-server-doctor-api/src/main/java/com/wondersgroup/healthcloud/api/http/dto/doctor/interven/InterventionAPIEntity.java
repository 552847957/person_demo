package com.wondersgroup.healthcloud.api.http.dto.doctor.interven;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by zhuchunliu on 2015/9/8.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterventionAPIEntity {

    private Integer count;
    private List<Info> info ;

    public InterventionAPIEntity(){

    }
    public InterventionAPIEntity(Integer count ,List<Info> info){
        this.count = count;
        this.info = info;
    }
    public class Info{
        private String abnormalid;// 异常主键
        private String name;// 患者姓名
        private String personcard;// 证件号码
        private String personcardAbbr;// 证件号码
        private String age;//年龄
        private String gender;//性别
        private String mobilephone;//联系方式
        private String excName;//异常类型
        private String remindDate;//提醒日期
        private String registerId;//用户主键

        public String getAbnormalid() {
            return abnormalid;
        }

        public void setAbnormalid(String abnormalid) {
            this.abnormalid = abnormalid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPersoncard() {
            return personcard;
        }

        public void setPersoncard(String personcard) {
            this.personcard = personcard;
        }

        public String getPersoncardAbbr() {
            return personcardAbbr;
        }

        public void setPersoncardAbbr(String personcardAbbr) {
            this.personcardAbbr = personcardAbbr;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getMobilephone() {
            return mobilephone;
        }

        public void setMobilephone(String mobilephone) {
            this.mobilephone = mobilephone;
        }

        public String getExcName() {
            return excName;
        }

        public void setExcName(String excName) {
            this.excName = excName;
        }

        public String getRemindDate() {
            return remindDate;
        }

        public void setRemindDate(String remindDate) {
            this.remindDate = remindDate;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Info> getInfo() {
        return info;
    }

    public void setInfo(List<Info> info) {
        this.info = info;
    }
}
