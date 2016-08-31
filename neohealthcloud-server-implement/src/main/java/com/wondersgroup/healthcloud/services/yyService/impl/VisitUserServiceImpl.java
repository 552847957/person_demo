package com.wondersgroup.healthcloud.services.yyService.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.common.utils.PropertiesUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.redis.config.RedisConfig;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.yyService.VisitUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;


/**
 * add by ys 2016-05-17
 */
@Service("visitUserService")
public class VisitUserServiceImpl implements VisitUserService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisConfig redisConfig;

    @Value("yyservice.img.host")
    private String yyImgHost;//yyservice.img.host
    @Value("yyservice.service.host")
    private String yyServiceHost;

    @Autowired
    private Environment env;

    private HttpRequestExecutorManager httpRequestExecutorManager = new HttpRequestExecutorManager(new OkHttpClient());


    private static final int TIME_24_HOUR = 24*60*60;

    /**
     * 拼接需要的header信息
     * @return
     */
    private String[] getRequestHeader(String personcard, Boolean get_real_data) {
        Jedis jedis = jedis();
        String cache_key = "health-yySercice-getYYUserUserInfo-"+personcard;

        String cacheInfo = "";
        try{
            if (!jedis.exists(cache_key) || get_real_data){
                Request request = new RequestBuilder().post().url(getTokenUrl())
                        .params(new String[]{"idCard", personcard}).build();
                JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
                JsonNode body = response.convertBody();
                String status = body.get("status").asText();
                if ("1".equals(status) && null != body.get("response")){
                    cacheInfo = body.get("response").get("userId").asText() + "," + body.get("response").get("token").asText();
                }
            }else {
                cacheInfo = jedis.get(cache_key);
            }
            if (StringUtils.isNotEmpty(cacheInfo)){
                jedis.set(cache_key, cacheInfo);
                jedis.expire(cache_key, TIME_24_HOUR);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            returnResource(jedis);
        }
        if (StringUtils.isEmpty(cacheInfo)){
            throw new CommonException(2021, "服务用户不存在");
        }
        String[] cacheHeaders = cacheInfo.split(",");
        String[] header = { "userId", cacheHeaders[0], "token", cacheHeaders[1]};
        return header;
    }

    public String[] getRequestHeaderByUid(String uid, Boolean get_real_data){
        String personcard = this.getBindPersoncard(uid);
        return this.getRequestHeader(personcard, get_real_data);
    }

    /**
     * 处理post请求
     * @return
     */
    public JsonNode postRequest(String uid, String url, String[] parm){
        String personcard = this.getBindPersoncard(uid);

        Request request = new RequestBuilder().post().url(url).params(parm).headers(getRequestHeader(personcard, false)).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode body = response.convertBody();
        if (response.code() == 200 && body.get("status").asText().equals("999")){
            //登陆失效
            request = new RequestBuilder().post().url(url).params(parm).headers(getRequestHeader(personcard, true)).build();
            response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
            body = response.convertBody();
        }
        if (response.code() != 200){
            String errorCodeMsg = "服务提供异常("+response.code()+")";
            throw new CommonException(2022, errorCodeMsg);
        }
        return body;
    }
    /**
     * 处理post请求
     * @return
     */
    public JsonNode postRequest(String url, String[] parm){

        Request request = new RequestBuilder().post().url(url).params(parm).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode body = response.convertBody();
        if (response.code() != 200){
            String errorCodeMsg = "服务提供异常("+response.code()+")";
            throw new CommonException(2022, errorCodeMsg);
        }
        return body;
    }

    private String getBindPersoncard(String userId){
        RegisterInfo person = userService.getOneNotNull(userId);
        String bindPersonCard = person.getBindPersoncard();
        if (StringUtils.isEmpty(bindPersonCard)){
            throw new CommonException(2021, "没有绑定医养融合的服务");
        }
        return bindPersonCard;
    }

    private Jedis jedis() {
        return redisConfig.redisConnectionFactory().getResource();
    }

    private void returnResource(Jedis jedis) {
        redisConfig.redisConnectionFactory();
    }

    private  String getTokenUrl() {
        return this.env.getProperty("yyservice.service.host")+"/rest/users/toElmuPPye.action";
    }
}
