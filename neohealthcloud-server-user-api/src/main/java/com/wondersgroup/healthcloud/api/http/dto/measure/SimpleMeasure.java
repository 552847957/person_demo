package com.wondersgroup.healthcloud.api.http.dto.measure;

/**
 * Created by Jeffrey on 16/8/17.
 */
public class SimpleMeasure {

    private String name;

    private String testTime;

    private String value;

    private String flag;

    public SimpleMeasure() {
    }

    public SimpleMeasure(String name, String testTime, String value, String flag) {
        this.name = name;
        this.testTime = testTime;
        this.value = value;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
