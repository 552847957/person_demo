package com.wondersgroup.healthcloud.api.configurations;

import com.wondersgroup.healthcloud.api.http.interceptor.DiseaseGateInterceptor;
import com.wondersgroup.healthcloud.common.http.exceptions.handler.DefaultExceptionHandler;
import com.wondersgroup.healthcloud.common.http.exceptions.handler.MissingParameterExceptionHandler;
import com.wondersgroup.healthcloud.common.http.exceptions.handler.ServiceExceptionHandler;
import com.wondersgroup.healthcloud.common.http.filters.RequestWrapperFilter;
import com.wondersgroup.healthcloud.common.http.filters.interceptor.RequestAccessTokenInterceptor;
import com.wondersgroup.healthcloud.common.http.filters.interceptor.RequestReplayDefenderInterceptor;
import com.wondersgroup.healthcloud.common.http.support.OverAuthExclude;
import com.wondersgroup.healthcloud.helper.push.area.PushAreaService;
import com.wondersgroup.healthcloud.helper.push.area.PushClientSelector;
import com.wondersgroup.healthcloud.jpa.repository.app.AppConfigurationInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
import com.wondersgroup.healthcloud.services.user.SessionUtil;
import com.wondersgroup.healthcloud.utils.security.ReplayAttackDefender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.DispatcherType;
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
 * <p/>
 * Created by zhangzhixiu on 15/11/19.
 */
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {


    @Autowired
    private Environment environment;

    @Autowired
    private ReplayAttackDefender defender;

    @Autowired
    private SessionUtil sessionUtil;

    private String getActiveProfile() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length != 0) {
            return profiles[0];
        } else {
            return null;
        }
    }



    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new MissingParameterExceptionHandler());
        exceptionResolvers.add(new ServiceExceptionHandler());
        exceptionResolvers.add(new DefaultExceptionHandler());//default exception handler
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Boolean isSandbox = "de".equals(getActiveProfile());
        OverAuthExclude overAuthExclude = new OverAuthExclude();
        registry.addInterceptor(new DiseaseGateInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new RequestReplayDefenderInterceptor(defender, isSandbox));
        registry.addInterceptor(new RequestAccessTokenInterceptor(sessionUtil, isSandbox, overAuthExclude));
//        registry.addInterceptor(new InternalAdminAPIInterceptor());
        super.addInterceptors(registry);
    }

    @Bean
    public FilterRegistrationBean requestWrapperFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RequestWrapperFilter());
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        return registration;
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE");
//    }

    @Bean
    public FilterRegistrationBean corsFilterRegistration() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        final FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public PushClientSelector pushClientSelector(AppConfigurationInfoRepository repository) {
        PushClientSelector selector = new PushClientSelector();
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