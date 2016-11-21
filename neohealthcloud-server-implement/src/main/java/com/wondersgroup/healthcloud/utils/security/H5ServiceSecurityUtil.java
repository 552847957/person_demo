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
public class H5ServiceSecurityUtil {

    @Autowired
    private ThirdPartyH5ServiceConfigurationRepository thirdPartyH5ServiceConfigurationRepository;

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    public String secureUrl(String originalUrl, Session session) {
        if (originalUrl != null && session != null && session.getUserId() != null && originalUrl.contains("hcserviceid")) {
            RegisterInfo account = registerInfoRepository.findOne(session.getUserId());
            if (account == null) {
                return originalUrl;
            }
            HttpUrl.Builder urlBuilder = HttpUrl.parse(originalUrl).newBuilder();
            HttpUrl url = HttpUrl.parse(originalUrl);
            String serviceId = url.queryParameter("hcserviceid");
            if (serviceId != null) {
                HttpUrl.Builder builder = url.newBuilder();
                builder.removeAllQueryParameters("hcserviceid");
                ThirdPartyH5ServiceConfiguration conf = thirdPartyH5ServiceConfigurationRepository.findOne(serviceId);
                if (conf == null) {
                    return originalUrl;
                } else {
                    String result = builder.build().toString();
                    result = StringUtils.replace(result, "${uid}", RSA.encryptByPrivateKey(account.getRegisterid(), conf.getPrivateKey()));
                    if (account.verified()) {
                        result = StringUtils.replace(result, "${idcard}", RSA.encryptByPrivateKey(account.getPersoncard(), conf.getPrivateKey()));
                    }
                    return result;
                }
            }
        }

        return originalUrl;
    }
}
