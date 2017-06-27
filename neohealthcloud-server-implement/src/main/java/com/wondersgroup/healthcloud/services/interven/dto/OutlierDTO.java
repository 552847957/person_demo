package com.wondersgroup.healthcloud.services.interven.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.enums.CriterionType;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by longshasha on 17/5/23.
 * 异常数据对象
 * 血糖、血压
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OutlierDTO {

    /**
     * 用户Id
     */
    private String registerId;

    /**
     * 测量日期 2017-04-26
     */
    private String testTime;

    /**
     * 测量时间段
     * 0 早餐前 1 早餐后 2 午餐前 3 午餐后 4 晚餐前 5 晚餐后 6睡前 7 凌晨 8 随机
     */
    private String testPeriod;

    /**
     * 血糖值(0 正常,1 偏高，2 偏低)
     */
    private String flag;

    /**
     * 血糖值
     */
    private Double fpgValue;

    /**
     * 测量时间 6:30
     */
    private String testHour;

    /**
     * 收缩压
     */
    private Integer systolic;

    /**
     * 舒张压
     */
    private Integer diastolic;

    /**
     * 收缩压 (0 正常,1 偏高，2 偏低)
     */
    private String systolicFlag;

    /**
     * //舒张压 (0 正常,1 偏高，2 偏低)
     */
    private String diastolicFlag;

    private String measureWay;//测试途径 1:手动输入2:全程设备输入3:蓝牙设备4:G端报告解析  1是有笔的那个图表，其他是另一个图标

    public String getFlag() {

        if (fpgValue == null || StringUtils.isBlank(getTestPeriod())) {
            return null;
        }
        CriterionType criterionType = calculator(fpgValue, getTestPeriod());
        return criterionType == null ? null : criterionType.index;
    }

    public static CriterionType calculator(double f, String p) {
        // 0 早餐前 1 早餐后 2 午餐前 3 午餐后 4 晚餐前 5 晚餐后 6睡前 7 凌晨 8 随机
        if (Arrays.asList("0", "2", "4").contains(p)) {
            return f < 4.4 ? CriterionType.LOW : f > 7.0 ? CriterionType.HIGH : CriterionType.ORDINARY;
        } else if (Arrays.asList("1", "3", "5", "6", "7", "8").contains(p)) {
            return f < 4.4 ? CriterionType.LOW : f > 10.0 ? CriterionType.HIGH : CriterionType.ORDINARY;
        }
        return null;
    }

    /**
     * (0 正常,1 偏高，2 偏低)
     *
     * @return
     */
    public Integer getSystolicFlag() {
        return Integer.valueOf(systolicCalculator(systolic == null ? 0 : systolic).index);
    }

    public Integer getDiastolicFlag() {
        return Integer.valueOf(diastolicCalculator(diastolic == null ? 0 : diastolic).index);
    }

    /**
     * 收缩压异常计算
     *
     * @param s
     *            收缩压值
     * @return CriterionType
     */
    public static CriterionType systolicCalculator(int s) {
        return s >= 90 ? (s < 140 ? CriterionType.ORDINARY : CriterionType.HIGH) : CriterionType.LOW;
    }

    /**
     * 舒张压异常计算
     *
     * @param d
     *            舒张压值
     * @return CriterionType
     */
    public static CriterionType diastolicCalculator(int d) {
        return d >= 60 ? (d < 90 ? CriterionType.ORDINARY : CriterionType.HIGH) : CriterionType.LOW;
    }

    public OutlierDTO(NeoFamIntervention neoFamIntervention) {
        this.registerId = neoFamIntervention.getRegisterId();
        this.testTime = neoFamIntervention.getWarnDate()==null?"":DateFormatter.dateFormat(neoFamIntervention.getWarnDate());
        this.testPeriod = neoFamIntervention.getTestPeriod();
        this.fpgValue = neoFamIntervention.getFpgValue();
        this.testHour = neoFamIntervention.getWarnDate()==null?"":DateFormatter.hourDateFormat(neoFamIntervention.getWarnDate());
        this.systolic = neoFamIntervention.getSystolic();
        this.diastolic = neoFamIntervention.getDiastolic();
        this.measureWay = neoFamIntervention.getMeasureWay();
    }
}
