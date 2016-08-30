package com.wondersgroup.healthcloud.services.assessment.impl;



import com.wondersgroup.healthcloud.services.assessment.MeasureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service("measureServiceImpl")
public class MeasureServiceImpl implements MeasureService {

    private static final Logger log = LoggerFactory.getLogger("SRTE");

	private static DecimalFormat d =new DecimalFormat("##.00");
	static{
		d.setRoundingMode(RoundingMode.DOWN);
	}


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


}
