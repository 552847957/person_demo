package com.wondersgroup.healthcloud.api.http.dto.push;

import com.wondersgroup.healthcloud.jpa.entity.push.PushPlan;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by zhuchunliu on 2016/8/27.
 */
@Data
public class PushPlanDTO {
    private Integer id;
    private String targetName; // 标签标题
    private String title; // 推送标题
    private String content;// 推送内容
    private Integer articleId; // 连接地址
    private String createTime; // 创建时间
    private String planTime; //计划推送时间
    private String operate;//操作 0:新增、1：编辑、2：通过、3：驳回、4：复制、5：查看、6：取消

    public PushPlanDTO(PushPlan plan){
        this.id = plan.getId();
        this.title = plan.getTitle();
        this.content = plan.getContent();
        this.articleId = plan.getArticleId();
        this.createTime = new DateTime(plan.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss");
        this.planTime = new DateTime(plan.getPlanTime()).toString("yyyy-MM-dd HH:mm:ss");
    }
}
