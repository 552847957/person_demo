package com.wondersgroup.healthcloud.api.configurations;

import com.wondersgroup.healthcloud.helper.push.area.PushAdminSelector;
import com.wondersgroup.healthcloud.jpa.repository.app.AppConfigurationInfoRepository;
import com.wondersgroup.healthcloud.utils.security.AppSecretKeySelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
 * Created by zhangzhixiu on 8/21/16.
 */
@Configuration
public class UserBeanConfiguration {

    @Bean
    public PushAdminSelector pushAdminSelector(AppConfigurationInfoRepository repository) {
        PushAdminSelector selector = new PushAdminSelector();
        selector.init(repository.getAll());
        return selector;
    }

    @Bean
    public AppSecretKeySelector appSecretKeySelector(AppConfigurationInfoRepository repository) {
        AppSecretKeySelector appSecretKeySelector = new AppSecretKeySelector(repository.getAll(), true);
        return appSecretKeySelector;
    }
}
