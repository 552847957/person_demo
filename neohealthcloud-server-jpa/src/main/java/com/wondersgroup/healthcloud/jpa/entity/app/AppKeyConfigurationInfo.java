package com.wondersgroup.healthcloud.jpa.entity.app;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/15/16.
 */
@Data
@Entity
@Table(name = "app_tb_key_configuration_info")
public class AppKeyConfigurationInfo {

    @Id
    private String id;
    private String name;
    @Column(name = "main_area")
    private String mainArea;
    @Column(name = "push_id_user")
    private String pushIdUser;
    @Column(name = "push_key_user")
    private String pushKeyUser;
    @Column(name = "push_secret_user")
    private String pushSecretUser;
    @Column(name = "push_id_doctor")
    private String pushIdDoctor;
    @Column(name = "push_key_doctor")
    private String pushKeyDoctor;
    @Column(name = "push_secret_doctor")
    private String pushSecretDoctor;
    @Column(name = "app_secret_key_user")
    private String appSerectKeyUser;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
