package com.wondersgroup.healthcloud.api.http.dto.doctor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.dic.DepartGB;

import java.util.List;

/**
 * Created by qiujun on 2015/9/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorDepartmentEntity {

    public List<DepartGB> getSubDepartments() {
        return subDepartments;
    }

    public void setSubDepartments(List<DepartGB> subDepartments) {
        this.subDepartments = subDepartments;
    }

    private List<DepartGB> subDepartments;

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getDel_flag() {
        return del_flag;
    }

    public void setDel_flag(String del_flag) {
        this.del_flag = del_flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsview() {
        return isview;
    }

    public void setIsview(String isview) {
        this.isview = isview;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    private String id;
    private String name;
    private String pid;
    private String sort;
    private String isview;
    private String del_flag;
    private String create_by;
    private String create_date;
    private String update_by;
    private String update_date;
    private String source_id;

    public DoctorDepartmentEntity(DepartGB department, List<DepartGB> subDepartments){
        this.id = department.getId();
        this.subDepartments = subDepartments;
        this.pid = department.getPid();
    }

}
