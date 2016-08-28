package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Yoda on 2015/9/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentAPIEntity {
    private String comment_id;
    private String doctor_id;
    private String name;
    private String reply_name;
    private String avatar;
    private String content;
    private String floor;
    private String ago;
    private Boolean reply_more;
    private List<CommentAPIEntity> reply_list;

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReply_name() {
        return reply_name;
    }

    public void setReply_name(String reply_name) {
        this.reply_name = reply_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }

    public List<CommentAPIEntity> getReply_list() {
        return reply_list;
    }

    public void setReply_list(List<CommentAPIEntity> reply_list) {
        this.reply_list = reply_list;
    }

    public Boolean getReply_more() {
        return reply_more;
    }

    public void setReply_more(Boolean reply_more) {
        this.reply_more = reply_more;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
