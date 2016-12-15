package com.wondersgroup.healthcloud.services.assessment;


import com.wondersgroup.healthcloud.services.assessment.dto.BloodGlucoseDTO;

import java.util.List;

public interface MeasureService {

	String checkdPressureSystolic(Integer value);

	String checkdPressureDiastolic(Integer value);

	String checkBMI(Double height, Double weight);

	Double calculateBMI(Double height, Double weight);

	List<BloodGlucoseDTO> getRecentAbnormalBloodGlucose(String personCards);

}
