package com.wondersgroup.healthcloud.services.assessment;


public interface MeasureService {

	String checkdPressureSystolic(Integer value);

	String checkdPressureDiastolic(Integer value);

	String checkBMI(Double height, Double weight);

	Double calculateBMI(Double height, Double weight);

}
