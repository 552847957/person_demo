package com.wondersgroup.healthcloud.services.yyService.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.common.http.utils.QueryMapUtils;
import com.wondersgroup.healthcloud.common.utils.PropertiesUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.redis.config.RedisConfig;
import com.wondersgroup.healthcloud.services.yyService.VisitDoctorService;
import com.wondersgroup.healthcloud.services.yyService.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * add by ys 2016-08-30
 */
@Service("visitDoctorService")
public class VisitDoctorServiceImpl implements VisitDoctorService {

    @Value("yyservice.img.host")
    private String yyImgHost;//yyservice.img.host

    @Autowired
    private RedisConfig redisConfig;

    private HttpRequestExecutorManager httpRequestExecutorManager = new HttpRequestExecutorManager(new OkHttpClient());

    private static String serviceImgBaseUri = PropertiesUtils.get("api.medicineSupport.service.img.url");
    private static String checkInUrl = PropertiesUtils.get("api.medicineSupport.service.url")+"/rest/admin/order/checkIn.action";
    private static String getTokenUrl = PropertiesUtils.get("api.medicineSupport.service.url")+"/rest/users/JyCCDYsFmg.action";
    private static String orderListUrl = PropertiesUtils.get("api.medicineSupport.service.url")+"/rest/users/orderAction!orderinfoList.action";

    private static String execDemoUrl = PropertiesUtils.get("api.medicineSupport.service.url")+"/rest/order/execDemo.action";
    private static String execDemoResultUrl = PropertiesUtils.get("api.medicineSupport.service.url")+
                        "/rest/order/orderApplyInfoAction!findOrderExecInfo.action";

    private static String submitExecDemoUrl = PropertiesUtils.get("api.medicineSupport.service.url")+
                        "/rest/order/orderApplyInfoAction!saveOrderExecInfo.action";

    private static String getUserInfoUrl = PropertiesUtils.get("api.medicineSupport.service.url")+ "/rest/admin/order/client.action";


    private static final int TIME_24_HOUR = 24*60*60;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    public YYDoctorInfo getYYDoctorUserInfo(String personcard, Boolean get_real_data) {
        Jedis jedis = jedis();
        String cache_key = "health-yySercice-getYYDoctorUserInfo-"+personcard;
        String infoJson="";
        YYDoctorInfo doctorInfo = null;
        try{
            if (!jedis.exists(cache_key) || get_real_data){

                Request request = new RequestBuilder().post().url(getTokenUrl)
                            .params(new String[]{"idCard", personcard}).build();
                JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
                JsonNode body = response.convertBody();
                String status = body.get("status").asText();
                if ("1".equals(status) && null != body.get("response")){
                    infoJson = body.get("response").toString();
                }
            }else {
                infoJson = jedis.get(cache_key);
            }
            if (StringUtils.isNotEmpty(infoJson)){
                jedis.set(cache_key, infoJson);
                jedis.expire(cache_key, TIME_24_HOUR);
                Gson gson = new Gson();
                doctorInfo = gson.fromJson(infoJson, YYDoctorInfo.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            returnResource(jedis);
        }
        if (null == doctorInfo){
            throw new CommonException(2021, "服务用户不存在");
        }
        return doctorInfo;
    }

    @Override
    public YYVisitUserInfo getElderInfo(String personcard, String elderid) {
        JsonNode body = this.postRequest(personcard, getUserInfoUrl, new String[]{"id", elderid});
        YYVisitUserInfo userInfo = null;
        if (body.get("status").asText().equals("1")){
            JsonNode jsonNode = body.get("response");
            Gson gson = new Gson();
            userInfo = gson.fromJson(jsonNode.toString(), YYVisitUserInfo.class);
            if (StringUtils.isNotEmpty(userInfo.getHeadIcon())){
                userInfo.setHeadIcon(serviceImgBaseUri + "/" + userInfo.getHeadIcon());
            }
        }else {
            return null;
        }
        return userInfo;
    }

    @Override
    public List<YYVisitOrderInfo> getOrderList(String personcard, Integer type, Integer page, Integer pageSize) {
        Map<String, Object> parm = new HashMap<>();
        parm.put("pageIndex", page-1);
        parm.put("pageSize", pageSize);
        parm.put("type", type);
        List<YYVisitOrderInfo> list = new ArrayList<>();
        serviceImgBaseUri = getServiceImgHost();
        try {
            JsonNode body = this.postRequest(personcard, orderListUrl, QueryMapUtils.convert(parm));
            if (body.get("status").asText().equals("1")){
                JsonNode jsonNode = body.get("response");
                Gson gson = new Gson();
                if (jsonNode.isArray() && jsonNode.size() > 0){
                    for (JsonNode objNode : jsonNode) {
                        try {
                            YYVisitOrderInfo orderInfo = gson.fromJson(objNode.toString(), YYVisitOrderInfo.class);
                            if (StringUtils.isNotEmpty(orderInfo.getSmallimg())){
                                orderInfo.setSmallimg(serviceImgBaseUri + "/" + orderInfo.getSmallimg());
                            }
                            String showTime;
                            if (StringUtils.isNotEmpty(orderInfo.getEndtime())){
                                //完成去完成时间
                                showTime = this.getServiceShowTime(orderInfo.getEndtime(), null);
                            }else {
                                showTime = this.getServiceShowTime(orderInfo.getRealyytime(), orderInfo.getRealyysjd());
                            }
                            List<YYVisitOrderInfo.OrderPhotos> orderPhotoses = orderInfo.gettOrderPhotos();
                            if (orderPhotoses != null && !orderPhotoses.isEmpty()){
                                for (YYVisitOrderInfo.OrderPhotos orderPhoto : orderPhotoses){
                                    orderPhoto.setPhotoaddress(serviceImgBaseUri + "/" + orderPhoto.getPhotoaddress());
                                }
                            }
                            orderInfo.setService_showtime(showTime);
                            orderInfo.setSign_distance(getServiceSignDistance());
                            list.add(orderInfo);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取服务签到距离
     */
    private Integer getServiceSignDistance(){
        return 1000;
    }

    private SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
    private String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    /**
     * 时间格式转换
     * @param dateTime 时间 2016-05-05 12:12:22
     * @param apm 上午/下午
     * @return
     */
    private String getServiceShowTime(String dateTime, String apm){
        String showTime = "";
        try {
            Date date = sdf.parse(dateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            w = w < 0 ? 0 : w;
            String week = weekDays[w];
            String day = dateFm.format(date);
            if (StringUtils.isEmpty(apm)){
                apm = date.getHours() < 12 ? "上午" : "下午";
            }else {
                if (apm.equalsIgnoreCase("pm")){
                    apm = "下午";
                }else if (apm.equalsIgnoreCase("am")){
                    apm = "上午";
                }
            }
            showTime = day + " " + week + " " + apm;
        }catch (Exception e){

        }
        return StringUtils.isEmpty(showTime) ? dateTime : showTime;
    }

    /**
     * 获取医生表单任务的执行结果
     * @param personcard
     * @param workOrderNo
     * @return
     */
    private Map<String, JsonNode> getExecDemoResult(String personcard, String workOrderNo){
        JsonNode body = this.postRequest(personcard, execDemoResultUrl, new String[]{"workOrderNo", workOrderNo});
        Map<String, JsonNode> resultMap = new HashMap<>();
        if (body.get("status").asText().equals("1")) {
            JsonNode jsonNode = body.get("response");
            if (jsonNode == null || !jsonNode.isArray() || jsonNode.size() == 0){
                return resultMap;
            }else {
                for (JsonNode objNode : jsonNode) {
                    String xh = objNode.get("xh").asText();
                    resultMap.put(xh, objNode);
                }
            }
        }else {
            return resultMap;
        }
        return resultMap;
    }

    @Override
    public List<YYExecDemoInfo> execDemo(String personcard, String fwnrid, String workOrderNo) {
        JsonNode body = this.postRequest(personcard, execDemoUrl, new String[]{"fwnrid", fwnrid});

        Map<String, JsonNode> resultMap = this.getExecDemoResult(personcard, workOrderNo);

        List<YYExecDemoInfo> list = new ArrayList<>();
        if (body.get("status").asText().equals("1")) {
            JsonNode jsonNode = body.get("response");
            if (jsonNode.isArray() && jsonNode.size() > 0) {
                for (JsonNode objNode : jsonNode) {
                    YYExecDemoInfo demoInfo = new YYExecDemoInfo();
                    demoInfo.setExecitem(objNode.get("execitem").asText());
                    demoInfo.setExectype(objNode.get("exectype").asText());
                    String xh = objNode.get("xh").asText();
                    demoInfo.setXh(xh);
                    demoInfo.setFwnrid(objNode.get("fwnrid").asText());
                    JsonNode execresult = objNode.get("execresult");
                    JsonNode dealResult = resultMap.containsKey(xh) ? resultMap.get(xh) : null;
                    //要服务的每一个小服务的执行列表 eq:第一步/第二步
                    if (execresult.isArray() && execresult.size() > 0) {
                        int tmp_i = 0;
                        for (JsonNode objNode2 : execresult){
                            tmp_i++;
                            YYExecDemoResultInfo resultInfo = new YYExecDemoResultInfo();
                            String title = objNode2.get(0).asText();
                            resultInfo.setTitle(title);
                            JsonNode items = objNode2.get(1);
                            if (null != items && items.isArray() && items.size()>0){
                                Gson gson = new Gson();
                                List<String> bodyItems = gson.fromJson(items.toString(), List.class);
                                resultInfo.setItem(bodyItems);
                            }
                            if (dealResult != null){
                                //去拼装医生提交的信息
                                String dealExecresult = dealResult.get("execresult").asText();
                                if (dealExecresult.length() >= tmp_i){
                                    resultInfo.setResult(dealExecresult.substring(tmp_i-1, tmp_i));
                                }
                                JsonNode dealExecmemo = dealResult.get("execmemo");
                                if (null != dealExecmemo && dealExecmemo.isArray() && dealExecmemo.size()>tmp_i-1){
                                    String execmemoTmp = dealExecmemo.get(tmp_i-1).asText();
                                    if (StringUtils.isEmpty(execmemoTmp) || "null".equalsIgnoreCase(execmemoTmp)){
                                        execmemoTmp = "";
                                    }
                                    resultInfo.setComment(execmemoTmp);
                                }else if (null != dealExecmemo && dealExecmemo.isTextual()){
                                    try {
                                        Gson gson = new Gson();
                                        List<String> comomons = gson.fromJson(dealExecmemo.textValue(), List.class);
                                        String execmemoTmp = "";
                                        if (comomons != null && comomons.size() > tmp_i-1 && !StringUtils.isEmpty(comomons.get(tmp_i-1))){
                                            execmemoTmp = comomons.get(tmp_i-1).equalsIgnoreCase("null") ? "" : comomons.get(tmp_i-1);
                                        }
                                        resultInfo.setComment(execmemoTmp);
                                    }catch (Exception e){
                                    }
                                }
                            }else {
                                resultInfo.setComment("");
                                resultInfo.setResult("0");
                            }
                            demoInfo.addExecresult(resultInfo);
                        }
                    }
                    list.add(demoInfo);
                }
            } else {
                return null;
            }
        }
        return list;
    }

    @Override
    public Boolean submitExecDemo(String personcard, String workOrderNo, String data) {
        String[] parm = new String[]{"workOrderNo", workOrderNo, "data", data};
        JsonNode body = this.postRequest(personcard, submitExecDemoUrl, parm);
        if (body.get("status").asText().equals("1")){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 处理post请求
     * @return
     */
    private JsonNode postRequest(String personcard, String url, String[] parm){
        YYDoctorInfo doctorInfo = this.getYYDoctorUserInfo(personcard, false);

        Request request = new RequestBuilder().post().url(url).params(parm).headers(getServiceHeade(doctorInfo)).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode body = response.convertBody();

        if (response.code() == 200 && body.get("status").asText().equals("999")){
            //登陆失效
            doctorInfo = this.getYYDoctorUserInfo(personcard, true);
            request = new RequestBuilder().post().url(url).params(parm).headers(getServiceHeade(doctorInfo)).build();
            response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
            body = response.convertBody();
        }
        if (response.code() != 200){
            String errorCodeMsg = "服务提供异常("+response.code()+")";
            throw new CommonException(2022, errorCodeMsg);
        }
        return body;
    }

    @Override
    public Boolean checkInVisitService(String personcard, String workOrderNo) {
        String[] parm = new String[]{"workOrderNo", workOrderNo};
        JsonNode body = this.postRequest(personcard, checkInUrl, parm);
        if (body.get("status").asText().equals("1")){
            return true;
        }else {
            throw new CommonException(2040, body.get("message").asText());
        }
    }

    /**
     * 获取图片host
     * @return
     */
    private String getServiceImgHost(){
        return StringUtils.isEmpty(yyImgHost) ? "" : yyImgHost;
    }

    /**
     * 拼接需要的header信息
     * @param yyDoctorInfo
     * @return
     */
    private String[] getServiceHeade(YYDoctorInfo yyDoctorInfo){
        return new String[]{"userId", yyDoctorInfo.getUserId(), "token", yyDoctorInfo.getToken()};
    }

    private Jedis jedis() {
        return redisConfig.redisConnectionFactory().getResource();
    }

    private void returnResource(Jedis jedis) {
        redisConfig.redisConnectionFactory();
    }

}
