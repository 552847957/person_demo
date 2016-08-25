package com.wondersgroup.healthcloud.utils.security;

import com.google.common.collect.ImmutableMap;
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
 * Created by zhangzhixiu on 8/25/16.
 */
public class AppSecretKeySelector {

    private Map<String, String> map;

    public AppSecretKeySelector(List<AppKeyConfigurationInfo> appInfos, Boolean isUser) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (AppKeyConfigurationInfo appInfo : appInfos) {
            if (isUser && appInfo.getAppSerectKeyUser() != null) {
                builder.put(appInfo.getMainArea(), appInfo.getAppSerectKeyUser());
            }
            if ((!isUser) && appInfo.getAppSecretKeyDoctor() != null) {
                builder.put(appInfo.getMainArea(), appInfo.getAppSecretKeyDoctor());
            }
        }
        map = builder.build();
    }

    public String get(String area) {
        String key = map.get(area);
        if (key == null) {
            throw new AppNotExistException();
        }
        return key;
    }
}
