package com.wondersgroup.healthcloud.services.bbs.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.common.appenum.SysMsgTypeEnum;
import com.wondersgroup.healthcloud.common.utils.ArraysUtil;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by ys on 2016/08/21.
 * @author ys
 */
@Component
public class BbsMsgHandler {

    @Value("${JOB_CONNECTION_URL}")
    private String jobClientUrl;

    private static final Logger logger = LoggerFactory.getLogger("exlog");

    private final HttpRequestExecutorManager httpRequestExecutorManager = new HttpRequestExecutorManager(new OkHttpClient());

    private void requestGet(String url,String[] parm){
        try {
            Request request = new RequestBuilder().post().url(url).params(parm).build();
            JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
            JsonNode body = response.convertBody();
        }catch (Exception e){
            logger.info("bbs msq notify error : " + url);
            logger.info(Exceptions.getStackTraceAsString(e));
        }
    }

    /**
     * 用户禁言
     */
    public void userBan(String uid, String admin_uid, Integer banStatus, Integer banLogId){
        //设为正常,和永久禁言,不需要走定时任务
        //通知lts禁言多久
        if (banStatus.intValue() != UserConstant.BanStatus.OK && banStatus.intValue() != UserConstant.BanStatus.FOREVER){
            String url = jobClientUrl + "/api/bbs/userBan";
            String[] parms = new String[]{"uid", uid, "banStatus", String.valueOf(banStatus), "banLogId", String.valueOf(banLogId)};
            requestGet(url, parms);
        }

        //更新消息
        String msg_type = banStatus.intValue() == UserConstant.BanStatus.OK ? SysMsgTypeEnum.msgType6.value() : SysMsgTypeEnum.msgType5.value();
        String url = jobClientUrl + "/api/bbs/msg/push/sysnotice/userlocking";
        String[] parms = new String[]{"uid", uid, "mid", admin_uid, "msg_type", msg_type, "ban_id", String.valueOf(banLogId)};
        requestGet(url, parms);
    }

    /**
     * 发布新话题
     */
    public void publishTopic(String uid, Integer topicId){
        String url = jobClientUrl + "/api/bbs/msg/push/dynamic";
        String[] parms = new String[]{"uid", uid, "topic_id", String.valueOf(topicId), "msg_type", "0"};
        requestGet(url, parms);
    }

    /**
     * 批量审核通过
     */
    public void publishMultTopics(Iterable<Integer> topicIds){
        String url = jobClientUrl + "/api/bbs/msg/push/dynamic";
        String[] parms = new String[]{"topic_ids", ArraysUtil.splitInts2Sting(topicIds, ","), "msg_type", "0"};
        requestGet(url, parms);
    }

    /**
     * 添加关注
     */
    public void addAttent(String attent_uid, String login_uid){
        String url = jobClientUrl + "/api/bbs/msg/push/sysnotice/fans";
        String[] parms = new String[]{"uid", attent_uid, "fans_uid", login_uid,
                "msg_type", SysMsgTypeEnum.msgType0.value()};
        requestGet(url, parms);
    }

    /**
     * 管理员删除话题
     */
    public void adminDelTopic(String topic_uid, Integer topic_id){
        String url = jobClientUrl + "/api/bbs/msg/push/sysnotice/topic";
        String[] parms = new String[]{"uid", topic_uid, "topic_id", String.valueOf(topic_id),
                "msg_type", SysMsgTypeEnum.msgType1.value()};
        requestGet(url, parms);
    }

    /**
     * 管理员删除话题
     */
    public void adminDelComment(String admin_uid, Integer comment_id){
        String url = jobClientUrl + "/api/bbs/msg/push/sysnotice/commentDel";
        String[] parms = new String[]{"admin_uid", admin_uid, "comment_ids", String.valueOf(comment_id),
                "msg_type", SysMsgTypeEnum.msgType9.value()};
        requestGet(url, parms);
    }

    /**
     * 管理员删除评论
     */
    public void adminDelComment(String admin_uid, List<Integer> comment_ids){
        String url = jobClientUrl + "/api/bbs/msg/push/sysnotice/commentDel";
        String[] parms = new String[]{"admin_uid", admin_uid, "comment_ids", Joiner.on(",").join(comment_ids),
                "msg_type", SysMsgTypeEnum.msgType9.value()};
        requestGet(url, parms);
    }

    /**
     * 管理员话题加精
     */
    public void adminSetTopicBest(String uid, Integer topic_id){
        String url = jobClientUrl + "/api/bbs/msg/push/sysnotice/topic";
        String[] parms = new String[]{"uid", uid, "topic_id", String.valueOf(topic_id),
                "msg_type", SysMsgTypeEnum.msgType2.value()};
        requestGet(url, parms);
    }

    /**
     * 话题有新的评论
     */
    public void topicNewReply(String uid, Integer topic_id){
        String url = jobClientUrl + "/api/bbs/msg/push/sysnotice/topic";
        String msg_type = SysMsgTypeEnum.msgType3.value();
        String[] parms = new String[]{"uid", uid, "topic_id", String.valueOf(topic_id), "msg_type", msg_type};
        requestGet(url, parms);
    }

    /**
     * 话题评论有新的评论
     */
    public void commentNewReply(String uid, Integer topic_id, String replier_id, Integer floor){
        String url = jobClientUrl + "/api/bbs/msg/push/sysnotice/topic";
        String msg_type = SysMsgTypeEnum.msgType4.value();
        String[] parms = new String[]{"uid", uid, "topic_id", String.valueOf(topic_id), "msg_type", msg_type,
                "replier_id",replier_id,"floor_id",String.valueOf(floor)};
        requestGet(url, parms);
    }
}
