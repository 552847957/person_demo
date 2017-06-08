package com.wondersgroup.healthcloud.services.question.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Dialogs {
    private Integer isCurrentDoctor = 0;
    private List<DoctorAndPations> dialogDetails = new ArrayList<DoctorAndPations>();
    public Date lastDoctorDate; //最后一个医生的回答时间

    public void setMyDialogDetails(List<DoctorAndPations> dialogDetails) {
        this.dialogDetails = dialogDetails;

        //找到当前对话中，医生的最大数据
        for (DoctorAndPations a : this.dialogDetails) {
             if(a instanceof  DoctorAnster){
                 if (lastDoctorDate == null) {
                     lastDoctorDate = a.sortDate;
                 } else {
                     if (a.sortDate.getTime() > lastDoctorDate.getTime()) {
                         lastDoctorDate = a.sortDate;
                     }
                 }
             }
        }
    }


}
