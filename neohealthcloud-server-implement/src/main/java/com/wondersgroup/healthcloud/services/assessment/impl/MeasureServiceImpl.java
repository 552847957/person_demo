package com.wondersgroup.healthcloud.services.assessment.impl;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.services.assessment.MeasureService;
import com.wondersgroup.healthcloud.services.assessment.dto.BloodGlucoseDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

@Service("measureServiceImpl")
public class MeasureServiceImpl implements MeasureService {

    private static final Logger log = LoggerFactory.getLogger("EX");

	private static DecimalFormat d =new DecimalFormat("##.00");
	static{
		d.setRoundingMode(RoundingMode.DOWN);
	}

	@Autowired
	private Environment environment;

	@Value("${internal.api.service.measure.url}")
	private String measure;

	private final static String MEASURE_URL="/api/measure/3.0/recentAbnormalBloodGlucose";

	private final static HttpRequestExecutorManager httpRequestExecutorManager = new HttpRequestExecutorManager(new OkHttpClient());

	/**异常值标志**/
	/**正常**/
	public static final String MEASURE_FLAG_NORMAL = "0";
	/**偏低**/
	public static final String MEASURE_FLAG_LOW = "1";
	/**偏高**/
	public static final String MEASURE_FLAG_HIGH = "2";
	/**超高**/
	public static final String MEASURE_FLAG_ULTRAHIGH = "3";

	private static final String patten="yyyy-MM-dd";

	@Override
	public  String checkdPressureSystolic(Integer value){
		if(value<=90){
			return MEASURE_FLAG_LOW;
		}
		if(value>=140){
			return MEASURE_FLAG_HIGH;
		}
		return MEASURE_FLAG_NORMAL;
	}

	@Override
	public  String checkdPressureDiastolic(Integer value){
		if(value<=60){
			return MEASURE_FLAG_LOW;
		}
		if(value>=90){
			return MEASURE_FLAG_HIGH;
		}
		return MEASURE_FLAG_NORMAL;
	}

	@Override
	public String checkBMI(Double height,Double weight){
		Double bmi =calculateBMI(height,weight);
		if(bmi<=18.5){
			return MEASURE_FLAG_LOW;
		}else if(bmi>18.5&&bmi<24){
			return MEASURE_FLAG_NORMAL;
		}else if(bmi>=24&&bmi<28){
			return MEASURE_FLAG_HIGH;
		}else if(bmi>=28){
			return MEASURE_FLAG_ULTRAHIGH;
		}
		return null;
	}

	@Override
	public Double   calculateBMI(Double height,Double weight){
		return Double.valueOf(d.format(weight/Math.pow((height/100), 2)));
	}

	@Override
	public List<BloodGlucoseDTO> getRecentAbnormalBloodGlucose(String personCards) {
		Request request = new RequestBuilder().get().url(measure+this.MEASURE_URL).
				params(new String[]{"personcards", StringUtils.join(personCards,",")}).build();
		JsonNodeResponseWrapper response = (JsonNodeResponseWrapper)httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
		JsonNode jsonNode = response.convertBody();
		if(200 == response.code() && 0 == jsonNode.get("code").asInt()){
			ObjectMapper mapper = new ObjectMapper();
			JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, BloodGlucoseDTO.class);
			try {
				return new ObjectMapper().readValue(jsonNode.get("data").toString(), javaType);
			}catch (Exception ex){
				log.error(ex.getMessage(),ex);
			}
		}
		return null;
	}


}
