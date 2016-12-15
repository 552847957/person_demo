package com.wondersgroup.healthcloud.utils.security;

import com.squareup.okhttp.HttpUrl;
import com.wondersgroup.healthcloud.jpa.entity.app.ThirdPartyH5ServiceConfiguration;
import com.wondersgroup.healthcloud.jpa.repository.app.ThirdPartyH5ServiceConfigurationRepository;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;

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
 * Created by zhangzhixiu on 21/11/2016.
 */
@Component
public class ServiceUrlPlaceholderResolver {

    @Autowired
    private ThirdPartyH5ServiceConfigurationRepository thirdPartyH5ServiceConfigurationRepository;


    public String parseUrl(String originalUrl, Session session) {
        try {
            if (originalUrl != null && session != null && session.getUserId() != null) {
                if (StringUtils.contains(originalUrl, "hcserviceid") && StringUtils.startsWith(originalUrl, "http")) {//第三方服务
                    HttpUrl url = HttpUrl.parse(originalUrl);
                    HttpUrl.Builder builder = url.newBuilder();
                    String serviceId = url.queryParameter("hcserviceid");
                    builder.removeAllQueryParameters("hcserviceid");
                    ThirdPartyH5ServiceConfiguration conf = thirdPartyH5ServiceConfigurationRepository.findOne(serviceId);
                    if (conf == null) {
                        return originalUrl;
                    } else {
                        String result = builder.build().toString();
                        return StringUtils.replace(result, "${uid}", URLEncoder.encode(RSA.encryptByPrivateKey(session.getUserId(), conf.getPrivateKey()), "UTF-8"));
                    }
                } else {
                    return StringUtils.replace(originalUrl, "${uid}", session.getUserId());
                }
            }
        } catch (Exception ex) {
            return originalUrl;
        }
        return originalUrl;
    }
}
