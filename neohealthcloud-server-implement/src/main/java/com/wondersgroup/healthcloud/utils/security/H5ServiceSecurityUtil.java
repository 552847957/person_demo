package com.wondersgroup.healthcloud.utils.security;

import com.squareup.okhttp.HttpUrl;
import com.wondersgroup.healthcloud.jpa.entity.app.ThirdPartyH5ServiceConfiguration;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.app.ThirdPartyH5ServiceConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

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

    private static final Pattern uidPattern = Pattern.compile("\\$\\{uid}");
    private static final Pattern idcardPattern = Pattern.compile("\\$\\{idcard}");

    public String secureUrl(String originalUrl, RegisterInfo account) {
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCy4GBC0FfLep4ue89SFiMvf8IVIor3fBQREkQuvO6CW+YIPkpBo65XwByzhVjtLQZomrclP4FcaBWEtsdQd2monNOcge7EOIaPINMYq0yiJje0aI2q5CWDUYEFbnx1F3/0l/eY6cU8alM44ibmEakCfpF/3v/tMdLXG5owJ2SgJilce9oVugiwZtEjMyYj4/ZOOxFQSUES+BpXgdn6/mj9qyUOrFgyBgib6tFbH4qS7rSHThY2HZkoZ9FXjiFVPwiLMTl7/ZC8m76dHK/eT4jHnPlbPAiJhprdwSREFdcsGE1YsnC67nrteYPjcsgPBdQxS/ius2DIAdGQW9D55h+HAgMBAAECggEBAJnyN4ZpLpYlwot47NXVzZRsnMl5wCX8uHx0vw+GdLLmipQfn+LcDwjggxMRuZXuASz3spWxERFJVvOwJtue2eVOi2SQAsEHcO8vrd32V27Z+2kd7obb8VkSRTs6eox/nBUS0Pnef2xeiZ9UK2woWM+XxHtLnfEyxyVwUQQOczDMFGAH8Crb3po8/bi2mPH982xFAm9WEYcgWlceYixBd37eucc3yB1p4DqoeMaMBUY9aeu6d6BVotIi3oCphqW+Neuz9hDUpJgb+lveXzTwuqRfrPjwrkkyEXoA/ru3fEkSA481Cx8N995OQUFg2npzpzV/BnbLCv+xbSXF1GAGJWECgYEA7+I9AAoI6Ls1ZXsRIDby6GB39rgDFBskjZBDyDnVq7B+Ss5owac9aq0AKHTE7srwWHBwUUSkvODI/RCvbyghfd/lbCAhNdPmur3nxvUUr7qxEEkCvPFFPKtdtH+dUtsdVfav7oxLbKbgs7ijmLcfrrMxwAr2LDb2A5dsUn0aE7cCgYEAvuTfkYtahL/qERibSpunHbRalibuBjznApDySxgqFFul+JodjBH/gX7Xnh/ssINcG3Fq3UNKj0/6SqCVQLlZrDqpohUoPcjtuGgTCoviywxEOWLqYh8HIMTTNHOe6Qdto9K7WlFYL0RN5fWh9O/n/aDAE7aJTjUPwjGC50ihcrECgYEA5kSoBaecUbuTUCzIjcHxfrtLrNuKqPw9JF79kfBieYLcWHa2/F3LiOE0q6EbgZXxDRQx3PElqeGlTbd8kBlXvPr1wcs91evpKg0ttkCAcQem/Fj5deGscsaVFrUBkg2fpWs+CqFFrbCrNhnhgLEYipyc/xoGoP7JPT1Xz/8izxUCgYB06OUHVSo9zO+EQuawfb2Okqs79GGoTlpIlw7c8NKsnyh5paFc7JTn8RAKlpC11e1uHTsOgazDMn8ef1SJTDrgIRlM29pGZK06V/r5ZYyjQEL0RmE3cLtD1WdoYzs6ikMapu/5M4JniFSQ9quWv+r7yRx5tjxHOnYJP5tlHHVMMQKBgEqTtpThsU7mld1w5bVsjOws+waTKH1GXust7DBuxILGJ4LODn+YAP2h1T8mNbiyGaE25V+Ct9EBdwFW73JFFj3kxSyGEqvonXkMF1CsgEiU+jeOXhOYAD+MF8zcekdxvY/7v1tl4sic+cCZdQJJ/pgIVY719A0zUKM8n7v8GyO/";
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
                String result = StringUtils.replace(builder.build().toString(), "${uid}", RSA.encryptByPrivateKey(account.getRegisterid(), conf.getPrivateKey()));
                return result;
            }
        }
        return originalUrl;
    }

    public static void main(String... args) {
        H5ServiceSecurityUtil util = new H5ServiceSecurityUtil();
        RegisterInfo info = new RegisterInfo();
        info.setRegisterid("1diaojfdioajfodisioasdf");
        System.out.println(util.secureUrl("http://localhost:8080/service?hcserviceid=1&uid=${uid}", info));
    }
}
