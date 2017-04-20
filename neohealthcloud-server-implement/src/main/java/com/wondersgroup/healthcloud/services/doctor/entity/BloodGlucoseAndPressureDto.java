package com.wondersgroup.healthcloud.services.doctor.entity;

import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import com.wondersgroup.healthcloud.utils.familyDoctor.FamilyDoctorUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by limenghua on 2017/4/20.
 */
@Data
public class BloodGlucoseAndPressureDto implements Serializable {

    private List<BloodGlucoseAbnormalDto> bloodGlucoseSuggest;
    private Object bloodPressureSuggest;

    public BloodGlucoseAndPressureDto() {
    }

    public BloodGlucoseAndPressureDto(List<DoctorIntervention> rtnList) {
        List<BloodGlucoseAbnormalDto> list = new ArrayList<>();
        if (rtnList != null && rtnList.size() > 0) {
            for (DoctorIntervention d : rtnList) {
                BloodGlucoseAbnormalDto dga = new BloodGlucoseAbnormalDto(d);
                list.add(dga);
            }// end for
        }// end if
        this.bloodGlucoseSuggest = list;
        // 血压暂时数据为空
        this.bloodPressureSuggest = "暂无建议";
    }// end method

    @Data
    public class BloodGlucoseAbnormalDto implements Serializable {
        private String testTime;// 测量时间
        private String fpgValue;// 血糖值
        private String interveneTime;// 干预时间（创建时间）
        private String doctorSuggest;// 医生建议内容

        /**
         * 区间 0 早餐前 1 早餐后 2 午餐前 3 午餐后 4 晚餐前 5 晚餐后 6睡前 7 凌晨 8 随机
         *
         * @param doctorIntervention
         */
        public BloodGlucoseAbnormalDto(DoctorIntervention doctorIntervention) {
            this.testTime = getFormatTestTime(doctorIntervention.getTestTime(), doctorIntervention.getTestPeriod());
            this.fpgValue = getShowFgpValue(doctorIntervention.getFpgValue(), doctorIntervention.getTestPeriod());
            this.interveneTime = DateUtils.format(doctorIntervention.getCreateTime(), "yyyy-MM-dd");
            this.doctorSuggest = doctorIntervention.getContent();
        }

        private String getShowFgpValue(String fpgValue, String testPeriod) {
            fpgValue = StringUtils.isBlank(fpgValue) ? "0" : fpgValue;
            String result = fpgValue;
            String iconHighLow;
            if ("0".equals(testPeriod) || "2".equals(testPeriod) || "4".equals(testPeriod)) {
                if (new BigDecimal("4.4").compareTo(new BigDecimal(fpgValue)) > 0) {// 偏低
                    iconHighLow = "↓";
                } else if (new BigDecimal("7.0").compareTo(new BigDecimal(fpgValue)) < 0) {// 偏高
                    iconHighLow = "↑";
                } else {// 正常
                    iconHighLow = "|";
                }
            } else {
                if (new BigDecimal("4.4").compareTo(new BigDecimal(fpgValue)) > 0) {// 偏低
                    iconHighLow = "↓";
                } else if (new BigDecimal("10.0").compareTo(new BigDecimal(fpgValue)) < 0) {// 偏高
                    iconHighLow = "↑";
                } else {// 正常
                    iconHighLow = "|";
                }
            }
            result = result + iconHighLow + "mmol/L";
            return result;
        }

        private String getFormatTestTime(String testTime, String testPeriod) {
            String strTime = "";
            if (StringUtils.isNotBlank(testTime)) {
                strTime = DateUtils.format(DateUtils.parseString(testTime), "yyyy-MM-dd");
            }
            String result = strTime + "  " + FamilyDoctorUtil.getStrInterval(testPeriod);
            return result;
        }

    }// end inner class

}
