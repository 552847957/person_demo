package com.wondersgroup.healthcloud.jpa.entity.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by dukuanxin on 2016/8/25.
 */
@Data
@Entity
@Table(name="")
public class ForwardArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int article_id;
    private String main_area;
    private String spec_area;
    private int rank;
    private int is_visable;
    private  Date start_time;
    private Date end_time;
    private Date create_time;
}
