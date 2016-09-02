package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MedicalCircleAPIEntity {
    private String           circle_id;   //帖子id
    private String           doctor_id;   //医生id
    private String           avatar;      //图像
    private String           name;        //名称
    private String           hospital;    //医院
    private String           ago;         //时间
    private String           tag;         //标签
    private Long             like_num;    //赞数量
    private Long             comment_num; //评论数
    private Boolean          is_liked;
    private Integer          circle_type; //圈子类型 1学术贴 2病例 3动态
    private NoteAPIEntity    note;        //帖子
    private CaseAPIEntity    cases;       //病例
    private DynamicAPIEntity dynamic;     //动态
    private String           color;

}
