package com.wondersgroup.healthcloud.api.http.dto.push;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by zhuchunliu on 2016/8/27.
 */
@Data
public class PushPlanDTO {
    private Integer id;
    @JsonProperty("tagName")
    private String targetName; // 标签标题
    private String title; // 推送标题
    private String content;// 推送内容
    @JsonProperty("articleId")
    private Integer articleId; // 连接地址
    @JsonProperty("topicId")
    private Integer topicId; // 连接地址
    @JsonProperty("createTime")
    private String createTime; // 创建时间
    @JsonProperty("planTime")
    private String planTime; //计划推送时间
    private String operate;//操作 1：编辑、2：通过、3：驳回、4：复制、5：查看、6：取消
    private Integer status ;// 0:待审核、1:待推送、2:已推送、3:已取消、4:已驳回、5:已过期

    public PushPlanDTO(PushPlan plan,String uid,Boolean audit){
        this.id = plan.getId();
        this.title = plan.getTitle();
        this.content = plan.getContent();
        this.articleId = plan.getArticleId();
        this.topicId = plan.getTopicId();
        this.createTime = new DateTime(plan.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss");
        this.planTime = new DateTime(plan.getPlanTime()).toString("yyyy-MM-dd HH:mm:ss");
        this.status = plan.getStatus();

        Set permission = Sets.newHashSet(4,5);
        switch (plan.getStatus()){
            case 0: //待审核
                if(audit){
                    permission.add(2);
                    permission.add(3);
                }
                break;
            case 1: //待推送
                if(StringUtils.equalsIgnoreCase(uid,plan.getCreator())){
                    permission.add(6);
                }
                break;
            case 4: //已经驳回
                if(StringUtils.equalsIgnoreCase(uid,plan.getCreator())){
                    permission.add(1);
                    permission.add(6);
                }
                break;
            default: //已推送、已取消、已驳回
                break;
        }
        this.operate = StringUtils.join(permission,",");
    }
}
