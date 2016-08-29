package com.wondersgroup.healthcloud.api.http.controllers.doctor.gw;

import com.wondersgroup.healthcloud.common.utils.JailPropertiesUtils;
import com.wondersgroup.healthcloud.utils.InterfaceEnCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 公卫API Rest client
 * @author jialing.yao  2016年6月23日
 */
@Component
public class GwRestClient {
	@Autowired
	public JailPropertiesUtils jailPropertiesUtils;
	@Autowired
    public RestTemplate restTemplate;
	
	/**
	 * 生成公卫token
	 */
	public String getGwToken(){
		return InterfaceEnCode.getAccessToken();
	}
	private HttpHeaders buildHeaders(){
	    HttpHeaders headers =  new HttpHeaders();
	    headers.add("access-token", getGwToken());
	    //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    return headers;
	}
	
	/**
	 * 获取签约接口地址
	 */
	public String getSignedConnUrl(){
		return jailPropertiesUtils.getGwWebSignedUrl();
	}
	/**
	 * 获取档案接口地址
	 */
	public String getArchConnUrl(){
		return jailPropertiesUtils.getGwWebSignedUrl();//todo 待添加地址
	}

	/**
	 * 待签约-批量获取居民档案状态、建档状态查询
	 */
	public String getBatchArchStatus(List<Map<String,String>> allIdCard){
		HttpHeaders headers =buildHeaders();
		headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
		formData.add("keys", allIdCard);
		String reqUrl=getArchConnUrl()+"/archive/api/archiveInfo";
		ResponseEntity<String> result= restTemplate.exchange(reqUrl, HttpMethod.POST, new HttpEntity<Object>(formData,headers), String.class);
		return result.getBody();
	}

	/**
	 * 待签约-基本信息查询
	 */
	public String getBaseInfo(Map<String,Object> input){
		String reqUrl=getSignedConnUrl()+"/api/infrastructure/dic/address?county={county}&town={town}";
		ResponseEntity<String> result= restTemplate.exchange(reqUrl, HttpMethod.GET, new HttpEntity<Object>(buildHeaders()), String.class, input);
		return result.getBody();
	}
	/**
	 * 待签约-机构信息查询
	 */
	public String getOrgInfo(Map<String,Object> input){
		String reqUrl=getSignedConnUrl()+"/api/infrastructure/dic/signhospitals?county={county}&level={level}";
		ResponseEntity<String> result= restTemplate.exchange(reqUrl, HttpMethod.GET, new HttpEntity<Object>(buildHeaders()), String.class, input);
		return result.getBody();
	}

	/**
	 * 待签约-保存签约
	 */
	public String saveSigned(String body){
		String reqUrl=getSignedConnUrl()+"/api/sign";
		ResponseEntity<String> result= restTemplate.exchange(reqUrl, HttpMethod.POST, new HttpEntity<Object>(body,buildHeaders()), String.class);
		return result.getBody();
	}

	/**
	 * 待签约-居民个人信息查询
	 */
	public String getPersonDetail(Map<String,Object> input){
		String reqUrl=getSignedConnUrl()+"/archive/api/get?personcardType={personcardType}&personcardNo={personcardNo}";
		ResponseEntity<String> result= restTemplate.exchange(reqUrl, HttpMethod.GET, new HttpEntity<Object>(buildHeaders()), String.class, input);
		return result.getBody();
	}
}
