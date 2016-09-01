package com.wondersgroup.healthcloud.api.http.controllers.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.app.AppKeyConfigurationInfo;
import com.wondersgroup.healthcloud.jpa.repository.app.AppConfigurationInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
 * Created by zhangzhixiu on 9/1/16.
 */
@RestController
public class AppSecretController {

    @Autowired
    private AppConfigurationInfoRepository appConfigurationInfoRepository;

    @GetMapping("/internal/secret/all")
    public JsonListResponseEntity<AppSecret> allAppSecret() {
        List<AppKeyConfigurationInfo> all = appConfigurationInfoRepository.getAll();

        List<AppSecret> result = Lists.newLinkedList();
        for (AppKeyConfigurationInfo appInfo : all) {
            if (appInfo.getAppSerectKeyUser() != null) {
                result.add(new AppSecret(appInfo.getMainArea(), appInfo.getAppSerectKeyUser(), true));
            }
            if (appInfo.getAppSecretKeyDoctor() != null) {
                result.add(new AppSecret(appInfo.getMainArea(), appInfo.getAppSecretKeyDoctor(), false));
            }
        }

        JsonListResponseEntity response = new JsonListResponseEntity<>();
        response.setContent(result);
        return response;
    }

    public static class AppSecret {
        public String area;
        public String key;
        @JsonProperty("is_user")
        public boolean isUser;

        public AppSecret() {

        }

        public AppSecret(String area, String key, boolean isUser) {
            this.area = area;
            this.key = key;
            this.isUser = isUser;
        }
    }
}
