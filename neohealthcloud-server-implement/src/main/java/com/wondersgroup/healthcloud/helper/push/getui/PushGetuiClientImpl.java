package com.wondersgroup.healthcloud.helper.push.getui;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.collect.Lists;
import com.wondersgroup.common.http.utils.JsonConverter;
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

    private String appId;
    private String appKey;
    private IGtPush push;

    public PushGetuiClientImpl(String appId, String appKey, String masterSecret) {
        this.appId = appId;
        this.appKey = appKey;
        this.push = new IGtPush("http://sdk.open.api.igexin.com/apiex.htm", appKey, masterSecret);
    }

    @Override
    public void pushToAll(PushMessage message) {
        TransmissionTemplate template = buildTemplate(message);
        AppMessage appMessage = new AppMessage();
        appMessage.setData(template);
        appMessage.setOffline(true);
        appMessage.setAppIdList(Lists.newArrayList(appId));
        IPushResult result = push.pushMessageToApp(appMessage);
        System.out.println(result.getResponse().toString());
    }

    @Override
    public void pushToAlias(PushMessage message, String alias) {
        TransmissionTemplate template = buildTemplate(message);
        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setOffline(true);
//        singleMessage.setOfflineExpireTime();
        singleMessage.setData(template);

        Target target = new Target();
        target.setAppId(appId);
        target.setAlias(alias);
        IPushResult result = push.pushMessageToSingle(singleMessage, target);
        System.out.println(result.getResponse().toString());
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
        IPushResult result = push.pushMessageToApp(appMessage);
        System.out.println(result.getResponse().toString());
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
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg(pushMessage.content));
        payload.addCustomMsg("content", jsonContent);

        template.setAPNInfo(payload);
        System.out.println(jsonContent);
        return template;
    }

    public static void main(String... args) {
        PushGetuiClientImpl client = new PushGetuiClientImpl("nEff3Tt7WbAtkHr0GZhgv4", "1DwS7XE9qN8hqmxRvYXk68", "mtKDMHtLGB9pXgNQkzm014");
//        client.pushToAll(PushMessage.Builder.init().title("测试").content("内容").param("aaa", "bbb").build());
        client.pushToAlias(PushMessage.Builder.init().title("测试别名").content("内容别名").url("com.wondersgroup.healthcloud.4401://user/test").param("aaa", "bbb").build(), "8a81c01a4ed91559014edd85bd4a000a");
//        System.out.println(JsonConverter.toJson(PushMessage.Builder.init().title("测试").content("内容").param("aaa", "bbb").build()));
    }
}
