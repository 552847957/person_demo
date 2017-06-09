package com.wondersgroup.healthcloud.api.http.dto.doctor.heathrecord;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wondersgroup.healthcloud.services.doctor.dto.BaseResidentDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by sunhaidi on 2017/6/1.
 */
@Data
public class HeathUserInfoDto extends BaseResidentDto{
   public  String  phone;
   @JsonFormat(pattern="yyyy-MM-dd")
   public  Date birth;
   public  String  cardType;
   public  String  cardNumber;
   public  String  profession;
   public  String  employStatus;
   public  String  mobilePhone;
   public  String  fixedPhone;
   public  String  contactPhone;
   public  String  medicarecard;

   public List<HeathIconDto> icons;




}
