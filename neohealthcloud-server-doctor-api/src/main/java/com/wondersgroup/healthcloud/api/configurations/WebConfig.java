package com.wondersgroup.healthcloud.api.configurations;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.jsonfilter.JsonReturnValueFilter;
import com.wondersgroup.healthcloud.common.http.exceptions.handler.DefaultExceptionHandler;
import com.wondersgroup.healthcloud.common.http.exceptions.handler.ServiceExceptionHandler;
import com.wondersgroup.healthcloud.common.http.filters.RequestWrapperFilter;
import com.wondersgroup.healthcloud.common.http.filters.interceptor.*;
import com.wondersgroup.healthcloud.common.http.support.OverAuthExclude;
import com.wondersgroup.healthcloud.common.http.support.jackson.PostJsonProcessor;
import com.wondersgroup.healthcloud.common.http.support.session.AccessTokenResolver;
import com.wondersgroup.healthcloud.common.http.support.session.SessionExceptionHandler;
import com.wondersgroup.healthcloud.common.http.support.version.VersionedRequestMappingHandlerMapping;
import com.wondersgroup.healthcloud.helper.push.area.PushAreaService;
import com.wondersgroup.healthcloud.helper.push.area.PushClientSelector;
import com.wondersgroup.healthcloud.jpa.repository.app.AppConfigurationInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.services.config.impl.AppConfigServiceImpl;
import com.wondersgroup.healthcloud.services.user.SessionUtil;
import com.wondersgroup.healthcloud.utils.security.AppSecretKeySelector;
import com.wondersgroup.healthcloud.utils.security.ReplayAttackDefender;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.DispatcherType;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private SessionUtil sessionService;

    @Autowired
    private ReplayAttackDefender defender;

    @Autowired
    private Environment environment;

    @Autowired
    private JsonReturnValueFilter jsonReturnValueFilter;

    private String getActiveProfile() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length != 0) {
            return profiles[0];
        } else {
            return null;
        }
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet ds = new DispatcherServlet();
        ds.setThrowExceptionIfNoHandlerFound(true);
        return ds;
    }

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping(AppSecretKeySelector appSecretKeySelector) {
        Boolean isSandbox = "de".equals(getActiveProfile()) || "te".equals(getActiveProfile());
        RequestMappingHandlerMapping handlerMapping = new VersionedRequestMappingHandlerMapping();
        List<Object> interceptorList = Lists.newLinkedList();
        interceptorList.add(new GateInterceptor());
        OverAuthExclude overAuthExclude = new OverAuthExclude();
        if (!"de".equals(getActiveProfile())) {
            interceptorList.add(new RequestTimeInterceptor(isSandbox));
            interceptorList.add(new RequestHeaderInterceptor(isSandbox));
            interceptorList.add(new RequestReplayDefenderInterceptor(defender, isSandbox));
            interceptorList.add(new RequestAccessTokenInterceptor(sessionService, isSandbox, overAuthExclude));
            interceptorList.add(new RequestSignatureInterceptor(sessionService, isSandbox, appSecretKeySelector));
        }
        handlerMapping.setInterceptors(interceptorList.toArray());
        return handlerMapping;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (int i = 0; i < converters.size(); i++) {
            if (converters.get(i) instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charsets.UTF_8);
                stringHttpMessageConverter.setWriteAcceptCharset(false);
                converters.set(i, stringHttpMessageConverter);
                break;
            }
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AccessTokenResolver(sessionService));
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers){

        PostJsonProcessor postJsonProcessor = new PostJsonProcessor(jsonReturnValueFilter);
        returnValueHandlers.add(postJsonProcessor);
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new SessionExceptionHandler());
        exceptionResolvers.add(new ServiceExceptionHandler());
        exceptionResolvers.add(new DefaultExceptionHandler());//default exception handler
    }


    @Bean
    public FilterRegistrationBean requestWrapperFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setOrder(0);
        registration.setFilter(new RequestWrapperFilter());
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        return registration;
    }

    @Bean//etag
    public FilterRegistrationBean etagFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setOrder(1);
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new ShallowEtagHeaderFilter());
        return registration;
    }


    @Bean
    public RestTemplate restTemplate() {
        // 长连接保持30秒
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
        // 总连接数
        pollingConnectionManager.setMaxTotal(1000);
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(1000);

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        // 重试次数，默认是3次，没有开启
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(2, true));
        // 保持长连接配置，需要在头添加Keep-Alive
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

        HttpClient httpClient = httpClientBuilder.build();

        // httpClient连接配置，底层是配置RequestConfig
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // 连接超时
        clientHttpRequestFactory.setConnectTimeout(10000);
        // 数据读取超时时间，即SocketTimeout
        clientHttpRequestFactory.setReadTimeout(10000);
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        clientHttpRequestFactory.setConnectionRequestTimeout(200);
        // 缓冲请求数据，默认值是true。通过POST或者PUT大发送数据时，建议将此属性更改为false，以免耗尽内存。
        // clientHttpRequestFactory.setBufferRequestBody(false);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof StringHttpMessageConverter) {
                messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
            } else {
                messageConverters.add(converter);
            }
        }
        restTemplate.setMessageConverters(messageConverters);
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
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