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

   //@Value("${api.measure.url}")
    private String API_MEASURE_URL;
    @Value("${api.userhealth.record.url}")
    private String API_USERHEALTH_RECORD_URL;

    public RestTemplate restTemplate = new RestTemplate();

    /** ***************************
     *  个人健康
     *  ***************************
     */
    public String userHealth(Map<String,Object> input){
        String reqUrl=API_MEASURE_URL+"/api/measure/userHealth?registerId={registerId}&sex={sex}&moreThanDays={moreThanDays}&limit={limit}&personCard={personCard}&cardType={cardType}&cardId={cardId}";
        return doGet(reqUrl,input);
    }

    public String userHealthRecord(Map<String,Object> input){
        String reqUrl=API_USERHEALTH_RECORD_URL+"/api/healthRecord/jiuzhen?idc={idc}";
        return doGet(reqUrl,input);
    }

    //查床添加、修改
    public String requestS01(String body){
        String reqUrl=API_MEASURE_URL+"/SaveBedCheck";
        return doPost(reqUrl,body);
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
