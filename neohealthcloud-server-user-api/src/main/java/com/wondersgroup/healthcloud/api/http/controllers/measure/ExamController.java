package com.wondersgroup.healthcloud.api.http.controllers.measure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;

/**
 * Created by Jeffrey on 16/8/29.
 */
@RestController
@RequestMapping("api/exam")
public class ExamController {

	private static final String requestStationNearby = "%s/api/exam/station/nearby?";
	private static final String requestStationDetail = "%s/api/exam/station/detail?id=%s";
	private static final String requestStationSearch = "%s/api/exam/station/search?";

	@Value("${internal.api.service.measure.url}")
	private String host;

	private RestTemplate template = new RestTemplate();

	@GetMapping(value = "station/nearby", produces = MediaType.APPLICATION_JSON_VALUE)
	@WithoutToken
	public JsonResponseEntity stationNearby(@RequestParam String areaCode, Double longitude, Double latitude,
			Boolean need, Integer flag) {
		String url = String.format(requestStationNearby, host) + "areaCode=" + areaCode;
		if (null != longitude && null != latitude) {
			url += "&longitude=" + longitude + "&latitude=" + latitude;
		}
		if (need != null) {
			url += "&need=" + need;
		}
		if (flag != null) {
			url += "&flag=" + flag;
		}

		ResponseEntity<Map> response = template.getForEntity(url, Map.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			Map result = response.getBody();
			int code = (int) result.get("code");
			if (code == 101) {
				return new JsonResponseEntity(101, (String) result.get("msg"));
			}
			return formatResponse(result);
		}
		return new JsonResponseEntity(500, "附近免费测量点获取失败");
	}

	@GetMapping("station/detail")
	@WithoutToken
	public JsonResponseEntity stationDetail(@RequestParam String id) {
		ResponseEntity<Map> response = template.getForEntity(String.format(requestStationDetail, host, id), Map.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return formatResponse(response.getBody());
		}
		return new JsonResponseEntity(500, "信息获取失败");
	}

	private JsonResponseEntity formatResponse(Map responseBody) {
		int code = (int) responseBody.get("code");
		if (0 != code) {
			return new JsonResponseEntity(500, "信息获取失败");
		}
		JsonResponseEntity<Object> result = new JsonResponseEntity<>(0, null);
		result.setData(responseBody.get("data"));
		return result;
	}

	@GetMapping(value = "station/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@WithoutToken
	public JsonResponseEntity search(@RequestParam String kw,String areaCode, Double longitude, Double latitude,
											Boolean need, Integer flag) {

		String url = String.format(requestStationSearch, host) + "kw="+kw+"&areaCode=" + areaCode;
		if (null != longitude && null != latitude) {
			url += "&longitude=" + longitude + "&latitude=" + latitude;
		}
		if (need != null) {
			url += "&need=" + need;
		}
		if (flag != null) {
			url += "&flag=" + flag;
		}

		ResponseEntity<Map> response = template.getForEntity(url, Map.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			Map result = response.getBody();
			int code = (int) result.get("code");
			if (code == 101) {
				return new JsonResponseEntity(101, (String) result.get("msg"));
			}
			return formatResponse(result);
		}
		return new JsonResponseEntity(500, "附近免费测量点获取失败");
	}

}
