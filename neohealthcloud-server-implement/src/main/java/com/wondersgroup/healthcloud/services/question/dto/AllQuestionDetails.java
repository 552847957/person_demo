package com.wondersgroup.healthcloud.services.question.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AllQuestionDetails {
    public AllQuestionDetails(){}
    public AllQuestionDetails(String id,String sex,int age,String content,String contentImgs,String date){
        this.id = id;
        this.sex = sex;
        this.age = age;
        this.content = content;
        this.contentImgs = contentImgs;
        this.date = date;
    }

    private String id; //问题ID
    private String sex; //性别
    private int age; //年龄
    private String content;//问题内容
    private String contentImgs;//图片
    private String date;
    List<List> dialogs = new ArrayList<List>(); //医生回答和患者追问集合
}
