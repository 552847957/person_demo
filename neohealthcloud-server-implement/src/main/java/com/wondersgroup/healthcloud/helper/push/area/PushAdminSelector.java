package com.wondersgroup.healthcloud.helper.push.area;

import com.google.common.collect.ImmutableMap;
import com.wondersgroup.healthcloud.helper.push.getui.PushAdminClient;
import com.wondersgroup.healthcloud.helper.push.getui.PushGetuiAdminClientImpl;
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
 * Created by zhangzhixiu on 8/16/16.
 */
public class PushAdminSelector {

    private Map<String, PushAdminClient> map;

    public void init(List<AppKeyConfigurationInfo> appInfos) {
        ImmutableMap.Builder<String, PushAdminClient> builder = ImmutableMap.builder();
        for (AppKeyConfigurationInfo appInfo : appInfos) {
            if (appInfo.getPushIdUser() != null) {
                builder.put(key(appInfo.getMainArea(), false), new PushGetuiAdminClientImpl(appInfo.getPushIdUser(), appInfo.getPushKeyUser(), appInfo.getPushSecretUser()));
            }
            if (appInfo.getPushIdDoctor() != null) {
                builder.put(key(appInfo.getMainArea(), true), new PushGetuiAdminClientImpl(appInfo.getPushIdDoctor(), appInfo.getPushKeyDoctor(), appInfo.getPushSecretDoctor()));
            }
        }
        map = builder.build();
    }

    private String key(String area, Boolean isDoctorSide) {
        return area + (isDoctorSide ? "d" : "");
    }

    public PushAdminClient getByArea(String area, Boolean isDoctorSide) {
        PushAdminClient client = map.get(key(area, isDoctorSide));
        if (client != null) {
            return client;
        } else {
            throw new RuntimeException();//todo
        }
    }
}
