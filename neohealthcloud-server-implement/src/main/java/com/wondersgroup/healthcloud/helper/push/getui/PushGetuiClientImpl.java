package com.wondersgroup.healthcloud.helper.push.getui;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.PushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.collect.Lists;
import com.wondersgroup.common.http.utils.JsonConverter;
import org.apache.commons.lang3.StringUtils;
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
 * Created by zhangzhixiu on 8/15/16.
 */
public class PushGetuiClientImpl implements PushClient {

    private static final Logger logger = LoggerFactory.getLogger(PushGetuiClientImpl.class);

    private String area;
    private String appId;
    private String appKey;
    private IGtPush push;

    public PushGetuiClientImpl(String area, String appId, String appKey, String masterSecret) {
        this.area = area;
        this.appId = appId;
        this.appKey = appKey;
        this.push = new IGtPush("http://sdk.open.api.igexin.com/apiex.htm", appKey, masterSecret);
    }

    @Override
    public String identityName() {
        return area;
    }

    @Override
    public void pushToAll(PushMessage message) {
        TransmissionTemplate template = buildTemplate(message);
        AppMessage appMessage = new AppMessage();
        appMessage.setData(template);
        appMessage.setOffline(true);
        appMessage.setAppIdList(Lists.newArrayList(appId));
        PushResult result = (PushResult) push.pushMessageToApp(appMessage);
        logger.info(String.format("cli[%s] msg[%s][%s] all result[%s]", area, result.getMessageId(), message.title, message.content), result.getResponse().toString());
    }

    @Override
    public void pushToAlias(PushMessage message, String alias) {
        pushToAliasWithExpireTime(message, alias, -1);
    }

    @Override
    public void pushToAliasWithExpireTime(PushMessage message, String alias, long expireTime) {
        TransmissionTemplate template = buildTemplate(message);
        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setOffline(true);
        if (expireTime >= 0) {
            singleMessage.setOfflineExpireTime(expireTime);
        }
        singleMessage.setData(template);

        Target target = new Target();
        target.setAppId(appId);
        target.setAlias(alias);
        PushResult result = (PushResult) push.pushMessageToSingle(singleMessage, target);
        logger.info(String.format("cli[%s] msg[%s][%s] alias[%s] url[%s] result[%s]", area, message.title, message.content, alias,message.url, result.getResponse().toString()));
    }

    @Override
    public void pushToTags(PushMessage message, List<String> tags) {
        TransmissionTemplate template = buildTemplate(message);
        AppMessage appMessage = new AppMessage();
        appMessage.setData(template);
        appMessage.setOffline(true);
        appMessage.setAppIdList(Lists.newArrayList(appId));
        AppConditions cdt = new AppConditions();
        cdt.addCondition(AppConditions.TAG, tags);
        appMessage.setConditions(cdt);
        PushResult result = (PushResult) push.pushMessageToApp(appMessage);
        logger.info(String.format("cli[%s] msg[%s][%s] tags[%s] result[%s]", area, message.title, message.content, StringUtils.join(tags, ","), result.getResponse().toString()));
    }

    private TransmissionTemplate buildTemplate(PushMessage pushMessage) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);

        String jsonContent = JsonConverter.toJson(pushMessage);
        template.setTransmissionContent(jsonContent);
        template.setTransmissionType(2);

        APNPayload payload = new APNPayload();
        payload.setBadge(1);
        payload.setContentAvailable(1);
        payload.setCategory("$由客户端定义");
        String msg = pushMessage.title + ": " + pushMessage.content;
        msg = msg.length() > 50 ? (StringUtils.substring(msg, 0, 50) + "...") : msg;
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg(msg));
        payload.addCustomMsg("content", jsonContent);

        template.setAPNInfo(payload);
        return template;
    }
}
