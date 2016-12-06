package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicCollect;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1. 添加收藏
 * 2. 获取收藏列表
 * @author yanshuai
 *
 */
@RestController
@RequestMapping("/api/bbs/collect")
public class TopicCollectController {

    private static final Integer PAGE_SIZE = 20;//每页个数20

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicCollectService topicCollectService;

    @VersionRange
    @RequestMapping(value = "/topicList", method = RequestMethod.GET)
    public JsonListResponseEntity<TopicListDto> topicList(@RequestParam String uid,
                                                  @RequestParam(required=false, defaultValue = "1") Integer flag){

        JsonListResponseEntity<TopicListDto> responseEntity = new JsonListResponseEntity<>();

        List<TopicCollect> collectList = topicCollectService.getCollectTopicListByUid(uid, flag, PAGE_SIZE);
        if (collectList == null || collectList.isEmpty()){
            responseEntity.setContent(null, false, null, String.valueOf(flag));
            return responseEntity;
        }
        List<Integer> topicIds = new ArrayList<>();
        Boolean hasMore = false;
        for (TopicCollect topicCollect : collectList){
            topicIds.add(topicCollect.getTopicId());
        }
        if (topicIds.size() > PAGE_SIZE){
            topicIds = topicIds.subList(0, PAGE_SIZE);
            hasMore = true;
        }
        List<TopicListDto> listDtos = topicService.getTopicsByIds(topicIds);
        if (listDtos != null && listDtos.size() > 1){
            Map<Integer, TopicListDto> dtoMap = new HashMap<>();
            for (TopicListDto listDto : listDtos){
                dtoMap.put(listDto.getId(), listDto);
            }
            listDtos.clear();
            for (Integer topicId : topicIds){
                if (dtoMap.containsKey(topicId)){
                    listDtos.add(dtoMap.get(topicId));
                }
            }
        }
        responseEntity.setContent(listDtos, hasMore, null, String.valueOf(flag+1));
        return responseEntity;
    }

    @VersionRange
    @RequestMapping(value = "/addTopic", method = RequestMethod.POST)
    public JsonResponseEntity<String> addTopic(@RequestBody String request){

        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(request);
        Integer topicId = reader.readInteger("topicId", false);
        String uid = reader.readString("uid", false);
        TopicCollect topicCollect = topicCollectService.collectTopic(uid, topicId);
        if (null == topicCollect){
            throw new CommonException(2021, "网络异常");
        }
        if (topicCollect.getDelFlag().equals("0")){
            body.setMsg("添加收藏成功");
        }else {
            body.setMsg("取消收藏成功");
        }
        return body;
    }

}
