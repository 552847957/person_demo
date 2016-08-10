package com.wondersgroup.healthcloud.api.configurations;

import com.squareup.okhttp.OkHttpClient;
import com.wondersgroup.common.jail.property.JailStartListener;
import com.wondersgroup.healthcloud.common.http.support.version.APIVersionChecker;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Properties;

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
 * <p/>
 * Created by zhangzhixiu on 15/11/16.
 */
@SpringBootApplication(scanBasePackages = "com.wondersgroup.healthcloud", exclude = {ErrorMvcAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.bannerMode(Banner.Mode.OFF).listeners(buildListener()).sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        preCheck();
        SpringApplication app = new SpringApplication(Application.class);
        app.setWebEnvironment(true);
        app.setBannerMode(Banner.Mode.OFF);
        app.addListeners(buildListener());
        app.run(args);
    }

    private static void preCheck() throws Exception {
        APIVersionChecker.check();
    }

    private static JailStartListener buildListener() {
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource("config/application-de.properties");
            Properties properties = new Properties();
            properties.load(resource.getInputStream());
            return new JailStartListener(new OkHttpClient(), properties.getProperty("jail.host"), properties.getProperty("jail.group"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
