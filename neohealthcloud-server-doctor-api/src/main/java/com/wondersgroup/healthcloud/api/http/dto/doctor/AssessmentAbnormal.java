package com.wondersgroup.healthcloud.api.http.dto.doctor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.services.assessment.AssessmentService;

@Data
public class AssessmentAbnormal {
    public String date;
    public String result;
    public List<String> cause;
    
    public AssessmentAbnormal() {
    }
    
    public AssessmentAbnormal(String date, String result, List<String> cause) {
        this.date = date;
        this.result = result;
        this.cause = cause;
    }
    
    public static List<String> cause(Assessment as, AssessmentService assessmentService){
        List<String> list = new ArrayList<String>();
        if(as == null ){
            return list;
        }
        if(as.getAge() != null && as.getAge() >= 40){
            list.add("年龄=" + as.getAge());
        }if(assessmentService.overWeight(as.getHeight(),as.getWeight())){
            list.add("超重/肥胖");
        }if(("1".equals(as.getGender()) && as.getWaist() >= 90) || ("2".equals(as.getGender()) && as.getWaist() >= 85) ){
            list.add("中心性肥胖");
        }if(as.getDiabetesRelatives() != null){
            list.add("亲属中有糖尿病患者");
        }if(as.getHypertensionRelatives() != null){
            list.add("亲属中有高血压患者");
        }if(as.getStrokeRelatives() != null){
            list.add("亲属中有脑卒中患者");
        }if(as.getIsDrink() != null && as.getIsDrink() == 2){
            list.add("经常喝酒");
        }if(as.getIsDrink() != null && as.getIsDrink() == 3){
            list.add("每天都喝酒");
        }if(as.getIsSmoking() != null && as.getIsSmoking() == 1){
            list.add("现在每天吸烟");
        }if(as.getIsSmoking() != null && as.getIsSmoking() == 2){
            list.add("现在吸烟，但不是每天吸烟");
        }if(as.getIsSmoking() != null && as.getIsSmoking() == 3){
            list.add("过去吸烟，现在不吸烟");
        }if(as.getEatHabits() != null && as.getEatHabits() == 2){
            list.add("饮食习惯荤食为主");
        }if(as.getEatHabits() != null && as.getEatHabits() == 3){
            list.add("饮食习惯素食为主");
        }if(as.getEatTaste() != null && "2".equals(as.getEatTaste())){
            list.add("饮食口味嗜油");
        }if(as.getEatTaste() != null && "1".equals(as.getEatTaste())){
            list.add("饮食口味嗜盐");
        }if(as.getEatTaste() != null && "3".equals(as.getEatTaste())){
            list.add("饮食口味嗜糖");
        }if(as.getSport() != null && as.getSport() == 4){
            list.add("严重缺乏运动");
        }if(as.getAge() != null && as.getAge() >= 40){
            list.add("血压" + as.getPressure() + "mmHg");
        }if(as.getIsDyslipidemia() != null && as.getIsDyslipidemia() == 1){
            list.add("血脂异常");
        }if(as.getMedicalHistory() != null && "1".equals(as.getMedicalHistory())){
            list.add("有糖调节受损");
        }if(as.getMedicalHistory() != null && "2".equals(as.getMedicalHistory())){
            list.add("动脉粥样硬化心脑血管疾病");
        }if(as.getMedicalHistory() != null && "3".equals(as.getMedicalHistory())){
            list.add("有过一性类固醇糖尿病");
        }if(as.getMedicalHistory() != null && "4".equals(as.getMedicalHistory())){
            list.add("房颤或明显的脉搏不齐");
        }if(as.getMedicalHistory() != null && "5".equals(as.getMedicalHistory())){
            list.add("短暂脑缺血发作病史");
        }if(as.getIsDepression() != null && as.getIsDepression() == 1){
            list.add("长期接受抗精神类药物");
        }if(as.getFemaleMedicalHistory() != null && "1".equals(as.getFemaleMedicalHistory())){
            list.add("有巨大儿");
        }if(as.getFemaleMedicalHistory() != null && "2".equals(as.getFemaleMedicalHistory())){
            list.add("有妊娠期糖尿病史");
        }if(as.getFemaleMedicalHistory() != null && "3".equals(as.getFemaleMedicalHistory())){
            list.add("多囊卵巢综合症患者");
        }
        return list;
    }

}
