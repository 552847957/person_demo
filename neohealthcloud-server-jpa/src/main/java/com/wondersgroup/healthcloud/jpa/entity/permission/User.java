package com.wondersgroup.healthcloud.jpa.entity.permission;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "tb_neopermission_user")
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;
    private String loginname;
    private String username;
    private String password;
    private String locked;
    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_by")
    private String updateBy;
    @Column(name = "update_date")
    private Date updateDate;
    @Column(name = "main_area")
    private String mainArea;
    @Column(name = "spec_area")
    private String specArea;
    @Transient
    private List<Role> roleList;

    public class Role{
        String roleId;
        String name;
        Boolean checked;

        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getChecked() {
            return checked;
        }

        public void setChecked(Boolean checked) {
            this.checked = checked;
        }
    }
}