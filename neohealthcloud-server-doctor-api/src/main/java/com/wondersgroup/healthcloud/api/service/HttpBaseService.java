package com.wondersgroup.healthcloud.api.service;

import com.google.common.collect.ImmutableList;
import com.wondersgroup.healthcloud.common.utils.JailPropertiesUtils;
import com.wondersgroup.healthcloud.utils.InterfaceEnCode;
import com.wondersgroup.healthcloud.utils.mapper.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class HttpBaseService {

	protected static Logger log = LoggerFactory.getLogger("EX");

	@Autowired
	JailPropertiesUtils jailPropertiesUtils;

	protected RestTemplate restTemplate = new RestTemplate();

	protected JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

	protected static String SIGNED_CONNECTION_URL;

//	protected static String ARCH_CONNECTION_URL;

	@PostConstruct
	public void init() {

		SIGNED_CONNECTION_URL = jailPropertiesUtils.getGwWebSignedUrl();
//		ARCH_CONNECTION_URL = jailPropertiesUtils.getGwWebSignedUrl();//todo 改地址

		ClientHttpRequestInterceptor httpRequestInterceptor = new HttpRequestInterceptor();
		restTemplate.setInterceptors(ImmutableList.of(httpRequestInterceptor));

	}

	protected String encode(String value) {
		try {
			value = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return value;
	}

	class HttpRequestInterceptor implements ClientHttpRequestInterceptor {

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
			requestWrapper.getHeaders().set("access-token", InterfaceEnCode.getAccessToken());
			return execution.execute(requestWrapper, body);
		}
	}

}
