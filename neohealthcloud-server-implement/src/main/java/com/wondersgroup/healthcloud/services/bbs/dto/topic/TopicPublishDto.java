package com.wondersgroup.healthcloud.services.bbs.dto.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ys on 2016/08/11.
 * @author ys
 * 发布话题需要的字段
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"topicTabs", "isAdmin"})
public class TopicPublishDto {

    private Boolean isAdminPublish = false;//是否是管理员发布

    private Integer id = 0;
    private String uid;
    private Integer circleId;
    private String title;
    private Integer isBest=0;//是否精华推荐
    private Integer isTop=0;
    private Integer topRank=0;
    private Integer isPublish=0;//是否发布
    private List<Integer> tags = new ArrayList<>();

    private List<TopicContent> topicContents = new ArrayList<>();
    private List<String> voteItems;

    public void setIsAdminPublish(Boolean isAdminPublish){
        this.isAdminPublish = isAdminPublish;
        if (!isAdminPublish){
            this.isPublish = 1;
        }
    }
    public void setTopicContents(List<Map<String, Object>> contents) {
        if (topicContents != null){
            for (Map<String, Object> info : contents){
                topicContents.add(new TopicContent(info));
            }
        }
    }

    public void addContent(String content, List<String> imgs){
        topicContents.add(new TopicContent(content, imgs));
    }

    @Data
    public class TopicContent{

        Integer id = 0;
        String content;
        List<String> imgs;

        TopicContent(Map<String, Object> info) {
            try {
                BeanUtils.populate(this, info);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        TopicContent(String content, List<String> imgs){
            this.content = content;
            this.imgs = imgs;
        }
    }

}
