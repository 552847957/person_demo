package com.wondersgroup.healthcloud.api.http.dto.appointment;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentContact;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import lombok.Data;

/**
 * Created by longshasha on 16/3/7.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDetailDTO {

    private String id;
    private String name;
    private String idcard;
    private String mobile;
    private String uid;

    @JsonProperty("is_default")
    private String isDefault;//是否默认

    /**
     * 医保卡
     */
    @JsonProperty("medi_card_id")
    private String mediCardId;


    public ContactDetailDTO() {
    }
    public ContactDetailDTO(String id) {
        this.id = id;
    }

    public ContactDetailDTO(AppointmentContact appointmentContact) {
        if(appointmentContact!=null){
            this.id = appointmentContact.getId();
            this.uid = appointmentContact.getUid();
            this.name = appointmentContact.getName();
            this.idcard = IdcardUtils.maskIdcard(appointmentContact.getIdcard());
            this.mobile = IdcardUtils.maskMobile(appointmentContact.getMobile());
            this.mediCardId = appointmentContact.getMediCardId();
            this.isDefault = appointmentContact.getIsDefault();
        }
    }



}
