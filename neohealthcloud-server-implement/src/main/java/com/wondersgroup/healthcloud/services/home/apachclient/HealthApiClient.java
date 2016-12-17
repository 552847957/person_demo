package com.wondersgroup.healthcloud.services.home.apachclient;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
/**
 * Created by xianglinhai on 2016/12/13.
 */
@Component
public class HealthApiClient {


    public RestTemplate restTemplate = new RestTemplate();

    /** ***************************
     *  查询个人健康
     *  ***************************
     */
    public String userHealth(String apiMeasureUrl,Map<String,Object> input){
        String reqUrl=apiMeasureUrl+"/api/measure/userHealth?registerId={registerId}&sex={sex}&moreThanDays={moreThanDays}&limit={limit}&personCard={personCard}&cardType={cardType}&cardId={cardId}";
        return doGet(reqUrl,input);
    }

    /**
     * 查询健康档案
     * @param apiUserhealthRecordUrl
     * @param input
     * @return
     */
    public String userHealthRecord(String apiUserhealthRecordUrl,Map<String,Object> input){
        String reqUrl=apiUserhealthRecordUrl+"/api/healthRecord/jiuzhen?idc={idc}";
        return doGet(reqUrl,input);
    }

    /**
     * 计算打疫苗提示时间
     * @param apiVaccineUrl
     * @param input
     * @return
     */
    public String getLeftDaysByBirth(String apiVaccineUrl,Map<String,Object> input){
        String reqUrl=apiVaccineUrl+"/api/vaccine/getLeftDaysByBirth?birthday={birthday}";
        return doGet(reqUrl,input);
    }


    private String doGet(String reqUrl,Map<String,Object> input){
        ResponseEntity<String> result= restTemplate.exchange(reqUrl, HttpMethod.GET, new HttpEntity<Object>(buildHeaders()), String.class, input);
        return convertToJsonIfXml(result);
    }
    private String doPost(String reqUrl,String body){
        HttpHeaders headers =buildHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("strRequest", body);
        ResponseEntity<String> result= restTemplate.exchange(reqUrl, HttpMethod.POST, new HttpEntity<Object>(formData,headers), String.class);
        return convertToJsonIfXml(result);
    }
    private HttpHeaders buildHeaders(){
        HttpHeaders headers =  new HttpHeaders();
        //headers.add("access-token", getGwToken());
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }
    private String convertToJsonIfXml(ResponseEntity<String> result){
        HttpHeaders headers = result.getHeaders();
        MediaType mediaType=headers.getContentType();
        String body=result.getBody();
        if(mediaType.equals(MediaType.TEXT_XML) || mediaType.getSubtype().equals("xml")){
            if(StringUtils.isNotBlank(body)){
                body=body.substring(body.indexOf("{"),body.lastIndexOf("}")+1);
            }
        }
        return body;
    }
}
