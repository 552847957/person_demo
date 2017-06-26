package com.wondersgroup.healthcloud.utils.familyDoctor;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.utils.InterfaceEnCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by longshasha on 16/6/24.
 */
public class FamilyDoctorUtil {

    private static final Logger logger = LoggerFactory.getLogger(FamilyDoctorUtil.class);

    private HttpRequestExecutorManager httpRequestExecutorManager;

    public void setHttpRequestExecutorManager(HttpRequestExecutorManager httpRequestExecutorManager) {
        this.httpRequestExecutorManager = httpRequestExecutorManager;
    }

    /**
     * 根据身份证号获取家庭医生信息
     * @param personcard
     * @return
     */
    public JsonNode getFamilyDoctorByUserPersoncard(String baseUrl, String personcard) {
        try {
            String url = baseUrl + "/api/sign/signdoctor";
            String[] form = new String[]{"personcard", personcard};
            String[] header = new String[]{"access-token", InterfaceEnCode.getAccessToken()};
            Request request = new RequestBuilder().get().url(url).params(form).headers(header).build();
            JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
            JsonNode result = response.convertBody();
            return result;
        } catch (Exception e) {
            logger.error("getFamilyDoctorByUserPersoncard error:", e);
            return null;
        }
    }

    /**
     * 0 早餐前 1 早餐后 2 午餐前 3 午餐后 4 晚餐前 5 晚餐后 6睡前 7 凌晨 8 随机
     * @param interval
     * @return
     */
    public static String getStrInterval(String interval) {
        String strInterval = "未知时间段";
        if (StringUtils.isBlank(interval)) {
            return strInterval;
        }
        switch (interval) {
            case "0":
                strInterval = "早餐前";
                break;
            case "1":
                strInterval = "早餐后";
                break;
            case "2":
                strInterval = "午餐前";
                break;
            case "3":
                strInterval = "午餐后";
                break;
            case "4":
                strInterval = "晚餐前";
                break;
            case "5":
                strInterval = "晚餐后";
                break;
            case "6":
                strInterval = "睡前";
                break;
            case "7":
                strInterval = "凌晨";
                break;
            case "8":
                strInterval = "随机";
                break;
            default:
                break;
        }
        return strInterval;
    }
}

