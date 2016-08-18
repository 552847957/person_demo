package com.wondersgroup.healthcloud.helper.push.area;

import com.google.common.collect.ImmutableMap;
import com.wondersgroup.healthcloud.helper.push.getui.PushClient;
import com.wondersgroup.healthcloud.helper.push.getui.PushGetuiClientImpl;
import com.wondersgroup.healthcloud.jpa.entity.app.AppKeyConfigurationInfo;

import java.util.List;
import java.util.Map;

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
public class PushClientSelector {

    private Map<String, PushClient> map;

    public void init(List<AppKeyConfigurationInfo> appInfos) {
        ImmutableMap.Builder<String, PushClient> builder = ImmutableMap.builder();
        for (AppKeyConfigurationInfo appInfo : appInfos) {
            if (appInfo.getPushIdUser() != null) {
                builder.put(key(appInfo.getMainArea(), false), new PushGetuiClientImpl(key(appInfo.getMainArea(), false), appInfo.getPushIdUser(), appInfo.getPushKeyUser(), appInfo.getPushSecretUser()));
            }
            if (appInfo.getPushIdDoctor() != null) {
                builder.put(key(appInfo.getMainArea(), true), new PushGetuiClientImpl(key(appInfo.getMainArea(), true), appInfo.getPushIdDoctor(), appInfo.getPushKeyDoctor(), appInfo.getPushSecretDoctor()));
            }
        }
        map = builder.build();
    }

    private String key(String area, Boolean isDoctorSide) {
        return area + (isDoctorSide ? "d" : "");
    }

    public PushClient getByArea(String area, Boolean isDoctorSide) {
        PushClient client = map.get(key(area, isDoctorSide));
        if (client != null) {
            return client;
        } else {
            throw new RuntimeException();//todo
        }
    }
}
