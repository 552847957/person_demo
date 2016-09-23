package com.wondersgroup.healthcloud.api.http.dto.doctor.interven;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2015/9/8.
 */
public class DoctorEntity {
    private String registerid;
    private String name;
    private String deptStandard;
    private String deptName;
    private String headphoto;
    private String dutyName;
    private String expertin;
    private String hospitalName;
    private String intro;
    private Boolean verified;
    private String personcard;
    private String mobile;
    private String talkid;
    private String talkpwd;
    private String talkgroupid;
    private Integer gender;
    private String nickname;
    private String town;
    private String hospitalId;
    private List<DoctorPrice> prices;// 专家积分

    public Integer getDocPrice(String module) {
        if (null == this.getPrices() || 0 == this.getPrices().size() || null == module) {
            return 0;
        } else {
            for (DoctorPrice price : this.getPrices()) {
                if (price.getDomain().equals(module)) {
                    return StringUtils.isEmpty(price.getPrice()) ? 0 : Integer.parseInt(price.getPrice());
                }
            }
        }
        return 0;
    }

    public Map<String,String> getDocMinPrice() {
        int value = 0 == this.getPrices().size() ? 0:Integer.parseInt(this.getPrices().get(0).getPrice());
        String module = 0 == this.getPrices().size() ? "0":this.getPrices().get(0).getDomain();
        Map<String,String> map = Maps.newHashMap();
        for (DoctorPrice price : this.getPrices()) {
            if(!StringUtils.isEmpty(price.getPrice()) && value > Integer.parseInt(price.getPrice())){
                value =Integer.parseInt(price.getPrice());
                module = price.getDomain();
            }
        }
        map.put("price",String.valueOf(value));
        map.put("module",module);
        return map;
    }

    public String getRegisterid() {
        return registerid;
    }

    public void setRegisterid(String registerid) {
        this.registerid = registerid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeptStandard() {
        return deptStandard;
    }

    public void setDeptStandard(String deptStandard) {
        this.deptStandard = deptStandard;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getHeadphoto() {
        return headphoto;
    }

    public void setHeadphoto(String headphoto) {
        this.headphoto = headphoto;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public String getExpertin() {
        return expertin;
    }

    public void setExpertin(String expertin) {
        this.expertin = expertin;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getPersoncard() {
        return personcard;
    }

    public void setPersoncard(String personcard) {
        this.personcard = personcard;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTalkid() {
        return talkid;
    }

    public void setTalkid(String talkid) {
        this.talkid = talkid;
    }

    public String getTalkpwd() {
        return talkpwd;
    }

    public void setTalkpwd(String talkpwd) {
        this.talkpwd = talkpwd;
    }

    public String getTalkgroupid() {
        return talkgroupid;
    }

    public void setTalkgroupid(String talkgroupid) {
        this.talkgroupid = talkgroupid;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public List<DoctorPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<DoctorPrice> prices) {
        this.prices = prices;
    }

    public class DoctorPrice implements Serializable {
        private String domain;
        private String price;

        public DoctorPrice() {
        }

        public DoctorPrice(String domain,String price) {
            this.domain = domain;
            this.price = price;
        }

        public String getPrice() {
            return this.price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDomain() {
            return this.domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }
}
