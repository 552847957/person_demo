package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.dto.VoteInfoDto;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 *  1. 话题投票
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/topic")
public class TopicVoteController {

    @Autowired
    private TopicVoteService topicVoteService;

    @Autowired
    private UserService userService;

    /**
     * 投票
     */
    @VersionRange
    @RequestMapping(value = "/vote", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> vote(@RequestBody String request){
        JsonResponseEntity<Map<String, Object>> rt = new JsonResponseEntity();

        Map<String, Object> info = new HashMap<>();

        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        Integer topicId = reader.readInteger("topicId", false);
        Integer voteId = reader.readInteger("voteId", false);
        Integer voteItemId = reader.readInteger("voteItemId", false);

        userService.getOneNotNull(uid);
        Boolean isVoted = topicVoteService.isVotedForUser(uid, voteId);
        if (isVoted){
            throw new CommonException(2002, "投票失败,您已经投过票啦!");
        }
        VoteInfoDto voteInfoDto = topicVoteService.submitVote(uid, voteId, voteItemId);
        info.put("voteInfo", voteInfoDto);
        rt.setData(info);
        rt.setMsg("投票成功");
        return rt;
    }

}
