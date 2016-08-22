package com.wondersgroup.healthcloud.jpa.entity.article;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2015/12/30.
 */
@Data
@Entity
@Table(name="app_tb_neoarticle")
public class NewsArticle implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;// 文章ID
    private String author;// 作者
    private String source;// 来源
    private String thumb;//缩略图
    private String title;//文章标题
    private String brief;//文章描述
    private String content;// 文章内容
    private String update_by;// 更新人
    private int fake_pv;// 虚拟阅读量
    private int pv;// 实际阅读量
    private Date online_time;// 上线时间
    private Date update_time;// 更新时间
    private String keyword;// 关键字
    private  String by_area;//创建区域
    private int type;//文章类型

}
