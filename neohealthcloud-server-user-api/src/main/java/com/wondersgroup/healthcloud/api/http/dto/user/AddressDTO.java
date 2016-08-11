package com.wondersgroup.healthcloud.api.http.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhangzhixiu on 7/13/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {
    private String province;
    private String city;
    private String county;
    private String town;
    private String committee;
    private String other;
    private String display;

    public AddressDTO() {

    }

    public AddressDTO(Address address, DictCache cache) {
        this.province = address.getProvince();
        this.city = address.getCity();
        this.county = address.getCounty();
        this.town = address.getTown();
        this.committee = address.getCommittee();
        this.other = address.getOther();
        StringBuffer buffer = new StringBuffer();
        buffer.append(StringUtils.trimToEmpty(cache.queryArea(this.province)));
        buffer.append(StringUtils.trimToEmpty(cache.queryArea(this.city)));
        buffer.append(StringUtils.trimToEmpty(cache.queryArea(this.county)));
        buffer.append(StringUtils.trimToEmpty(cache.queryArea(this.town)));
        buffer.append(StringUtils.trimToEmpty(cache.queryArea(this.committee)));
        buffer.append(StringUtils.trimToEmpty(this.other));
        this.display = StringUtils.trimToNull(buffer.toString());
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCommittee() {
        return committee;
    }

    public void setCommittee(String committee) {
        this.committee = committee;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
