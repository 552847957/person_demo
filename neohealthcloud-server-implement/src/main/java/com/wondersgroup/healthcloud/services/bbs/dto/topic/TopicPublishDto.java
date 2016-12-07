package com.wondersgroup.healthcloud.services.bbs.dto.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ys on 2016/08/11.
 * @author ys
 * 发布话题需要的字段
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicPublishDto {

    private Integer id;
    private String uid;
    private Integer circleId;
    private String title;
    private List<TopicContent> topicContents = new ArrayList<>();
    private List<String> voteItems;

    public void addContent(String content, List<String> imgs){
        topicContents.add(new TopicContent(content, imgs));
    }

    public class TopicContent{
        String content;
        List<String> imgs;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getImgs() {
            return imgs;
        }

        public void setImgs(List<String> imgs) {
            this.imgs = imgs;
        }

        TopicContent(String content, List<String> imgs){
            this.content = content;
            this.imgs = imgs;
        }
    }

}
