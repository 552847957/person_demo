package com.wondersgroup.healthcloud.api.http.dto.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentAPIEntity {
    private String                 comment_id; //评论id
    private String                 doctor_id;  //医生id
    private String                 name;       //名字
    private String                 reply_name; //回复名称
    private String                 avatar;     //图像
    private String                 content;    //内容
    private String                 floor;      //楼层
    private String                 ago;        //时间
    private Boolean                reply_more; //更多回复
    private List<CommentAPIEntity> reply_list; //回复集合

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
