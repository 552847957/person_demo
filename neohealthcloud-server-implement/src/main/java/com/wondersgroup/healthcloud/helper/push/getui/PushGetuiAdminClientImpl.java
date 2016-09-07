package com.wondersgroup.healthcloud.helper.push.getui;

import com.gexin.rp.sdk.base.IAliasResult;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.http.IGtPush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/16/16.
 */
public class PushGetuiAdminClientImpl implements PushAdminClient {

    private static final Logger logger = LoggerFactory.getLogger(PushGetuiAdminClientImpl.class);

    private String appId;
    private String appKey;
    private IGtPush push;

    public PushGetuiAdminClientImpl(String appId, String appKey, String masterSecret) {
        this.appId = appId;
        this.appKey = appKey;
        this.push = new IGtPush("http://sdk.open.api.igexin.com/apiex.htm", appKey, masterSecret);
    }

    @Override
    public void overrideTagToClient(String cid, List<String> tags) {
        IQueryResult ret = push.setClientTag(appId, cid, tags);
    }

    @Override
    public void unbindAliasAll(String alias) {
        IAliasResult AliasUnBindAll = push.unBindAliasAll(appId, alias);
    }

    @Override
    public void getPushResult(String taskId) {
        System.out.println(push.getPushResult(taskId).getResponse());
        IPushResult result = push.getPushResult(taskId);
        String msgTotal = result.getResponse().get("msgTotal").toString();
        String clickNum = result.getResponse().get("clickNum").toString();
        String msgProcess = result.getResponse().get("msgProcess").toString();
        System.out.println("总下发数:" + msgTotal + "|点击数:" + clickNum + "|下发的消息总数:" + msgProcess);
    }
}
