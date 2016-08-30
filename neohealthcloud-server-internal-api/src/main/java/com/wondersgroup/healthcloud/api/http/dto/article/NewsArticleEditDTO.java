package com.wondersgroup.healthcloud.api.http.dto.article;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * Created by dukuanxin on 2016/8/29.
 */
@Data
public class NewsArticleEditDTO {

    private String id;
    private String category_ids;
    private  int article_id;
    private String is_visable;

}
