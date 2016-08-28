package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Yoda on 2015/9/8.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorAPIEntity {
    private String doctor_id;
    private String avatar;
    private String name;
    private String hospital;
    private Boolean is_attention;
    private Long dynamic_num;
    private Long notecase_num;
    private Long attention_num;
    private Long fans_num;

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public Boolean getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(Boolean is_attention) {
        this.is_attention = is_attention;
    }

    public Long getDynamic_num() {
        return dynamic_num;
    }

    public void setDynamic_num(Long dynamic_num) {
        this.dynamic_num = dynamic_num;
    }

    public Long getNotecase_num() {
        return notecase_num;
    }

    public void setNotecase_num(Long notecase_num) {
        this.notecase_num = notecase_num;
    }

    public Long getAttention_num() {
        return attention_num;
    }

    public void setAttention_num(Long attention_num) {
        this.attention_num = attention_num;
    }

    public Long getFans_num() {
        return fans_num;
    }

    public void setFans_num(Long fans_num) {
        this.fans_num = fans_num;
    }
}
