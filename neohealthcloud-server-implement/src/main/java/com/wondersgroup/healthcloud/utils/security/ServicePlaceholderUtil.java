package com.wondersgroup.healthcloud.utils.security;

import com.squareup.okhttp.HttpUrl;
import com.wondersgroup.healthcloud.jpa.entity.app.ThirdPartyH5ServiceConfiguration;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.app.ThirdPartyH5ServiceConfigurationRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
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
public class ServicePlaceholderUtil {

    @Autowired
    private ThirdPartyH5ServiceConfigurationRepository thirdPartyH5ServiceConfigurationRepository;

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    public String secureUrl(String originalUrl, Session session) {
        try {
            if (originalUrl != null && session != null && session.getUserId() != null) {
                RegisterInfo account = registerInfoRepository.findOne(session.getUserId());
                if (account == null) {
                    return originalUrl;
                }

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
                        result = StringUtils.replace(result, "${uid}", URLEncoder.encode(RSA.encryptByPrivateKey(session.getUserId(), conf.getPrivateKey()), "UTF-8"));
                        if (account.verified()) {
                            result = StringUtils.replace(result, "${idcard}", URLEncoder.encode(RSA.encryptByPrivateKey(account.getPersoncard(), conf.getPrivateKey()), "UTF-8"));
                        }
                        return result;
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
