package com.wondersgroup.healthcloud.services.article.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * Created by dukuanxin on 2016/8/26.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForwardArticleAPIEntity {
    private int id;
    private int article_id;
    private int is_visable;
    private int rank;
    private Date start_time;
    private Date end_time;
    private String status;

    public ForwardArticleAPIEntity(Map<String,Object> param) {
        this.id = (int) param.get("id");
        this.article_id = (int) param.get("article_id");
        this.is_visable = (int) param.get("is_visable");
        this.rank = (int) param.get("rank");
        this.start_time = (Date) param.get("start_time");
        this.end_time = (Date)param.get("end_time");
        Date nowDate=new Date();
        this.status = "进行中";
        if (nowDate.before(start_time)){
            status = "未开始";
        }else if (nowDate.after(end_time)){
            status = "已结束";
        }
    }

}
