package com.wondersgroup.healthcloud.api.configurations;

import com.wondersgroup.healthcloud.helper.push.area.PushAdminSelector;
import com.wondersgroup.healthcloud.helper.push.area.PushAreaService;
import com.wondersgroup.healthcloud.helper.push.area.PushClientSelector;
import com.wondersgroup.healthcloud.jpa.repository.app.AppConfigurationInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
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
public class InternalBeanConfiguration {

    @Bean
    public PushClientSelector pushClientSelector(AppConfigurationInfoRepository repository) {
        PushClientSelector selector = new PushClientSelector();
        selector.init(repository.getAll());
        return selector;
    }

    @Bean
    public PushAdminSelector pushAdminSelector(AppConfigurationInfoRepository repository) {
        PushAdminSelector selector = new PushAdminSelector();
        selector.init(repository.getAll());
        return selector;
    }

    @Bean
    public PushAreaService pushAreaService(UserPushInfoRepository userPushInfoRepository,
                                           PushClientSelector pushClientSelector) {
        PushAreaService pushAreaService = new PushAreaService();
        pushAreaService.setPushClientSelector(pushClientSelector);
        pushAreaService.setUserPushInfoRepository(userPushInfoRepository);
        return pushAreaService;
    }
}
