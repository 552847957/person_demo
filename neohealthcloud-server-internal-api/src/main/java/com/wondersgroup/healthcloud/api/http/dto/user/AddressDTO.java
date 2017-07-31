package com.wondersgroup.healthcloud.api.http.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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


    @JsonProperty("province_name")
    private String provinceName;

    @JsonProperty("city_name")
    private String cityName;

    @JsonProperty("county_name")
    private String countyName;

    @JsonProperty("town_name")
    private String townName;

    @JsonProperty("committee_name")
    private String committeeName;

    public AddressDTO() {

    }

    public AddressDTO(Address address, DictCache cache) {
        this.province = address.getProvince();
        this.city = address.getCity();
        this.county = address.getCounty();
        this.town = address.getTown();
        this.committee = address.getCommittee();
        this.other = address.getOther();

        this.provinceName = StringUtils.trimToEmpty(cache.queryArea(this.province));
        this.cityName = StringUtils.trimToEmpty(cache.queryArea(this.city));
        this.countyName = StringUtils.trimToEmpty(cache.queryArea(this.county));
        this.townName = StringUtils.trimToEmpty(cache.queryArea(this.town));
        this.committeeName = StringUtils.trimToEmpty(cache.queryArea(this.committee));


        StringBuffer buffer = new StringBuffer();
        buffer.append(StringUtils.trimToEmpty((this.provinceName)));
        buffer.append(StringUtils.trimToEmpty((this.cityName)));
        buffer.append(StringUtils.trimToEmpty((this.countyName)));
        buffer.append(StringUtils.trimToEmpty((this.townName)));
        buffer.append(StringUtils.trimToEmpty((this.committeeName)));
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

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }
}
