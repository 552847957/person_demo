package com.wondersgroup.healthcloud.api.http.dto.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Yoda on 2015/9/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteCaseSearchResultAPIEntity {
    private String circle_id;
    private String doctor_id;
    private String avatar;
    private String name;
    private String hospital;
    private String ago;
    private String tag;
    private Long like_num;
    private Long comment_num;
    private Integer circle_type; //圈子类型 1学术贴 2病例 3动态
    private NoteAPIEntity note; //帖子
    private CaseAPIEntity cases; //病例

    public String getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(String circle_id) {
        this.circle_id = circle_id;
    }

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

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getLike_num() {
        return like_num;
    }

    public void setLike_num(Long like_num) {
        this.like_num = like_num;
    }

    public Long getComment_num() {
        return comment_num;
    }

    public void setComment_num(Long comment_num) {
        this.comment_num = comment_num;
    }

    public Integer getCircle_type() {
        return circle_type;
    }

    public void setCircle_type(Integer circle_type) {
        this.circle_type = circle_type;
    }

    public NoteAPIEntity getNote() {
        return note;
    }

    public void setNote(NoteAPIEntity note) {
        this.note = note;
    }

    public CaseAPIEntity getCases() {
        return cases;
    }

    public void setCases(CaseAPIEntity cases) {
        this.cases = cases;
    }
}
