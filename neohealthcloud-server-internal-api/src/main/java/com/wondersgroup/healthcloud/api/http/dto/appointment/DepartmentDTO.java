package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentL1Department;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentL2Department;

/**
 * Created by longshasha on 16/5/21.
 * 后台管理
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentDTO {

    private String id;
    private String name;
    private String level;


    public DepartmentDTO(AppointmentL1Department l1Department){
        if(l1Department!=null){
            this.id = l1Department.getId();
            this.name = l1Department.getDeptName();
            this.level = "1";
        }

    }

    public DepartmentDTO(AppointmentL2Department l2Department){
        if(l2Department!=null){
            this.id = l2Department.getId();
            this.name = l2Department.getDeptName();
            this.level = "2";
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

}
