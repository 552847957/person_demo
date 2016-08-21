package com.wondersgroup.healthcloud.jpa.entity.measure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wondersgroup.healthcloud.jpa.enums.MeasureType;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Created by Jeffrey on 16/8/21.
 */
@Data
@Entity
@JsonIgnoreProperties("new")
@Table(name = "app_tb_measure_management")
@EntityListeners(AuditingEntityListener.class)
public class MeasureManagement extends AbstractPersistable<Long> {

    private String title;

    @Column(name = "description")
    private String desc;

    @Column(name = "icon_url")
    private String iconUrl;

    @Enumerated(value = EnumType.ORDINAL)
    private MeasureType type;

    /**
     * 显示开关
     */
    private boolean display;

    /**
     * 排序权值
     */
    private Integer priorities;

    @CreatedDate
    @Temporal(TIMESTAMP)
    private Date createdDate;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    private Date lastModifiedDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

}
