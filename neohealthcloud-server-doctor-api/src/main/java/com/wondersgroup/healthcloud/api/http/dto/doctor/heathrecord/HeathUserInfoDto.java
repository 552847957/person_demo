package com.wondersgroup.healthcloud.api.http.dto.doctor.heathrecord;

import com.wondersgroup.healthcloud.services.doctor.dto.BaseResidentDto;
import lombok.Data;

import java.util.List;

/**
 * Created by sunhaidi on 2017/6/1.
 */
@Data
public class HeathUserInfoDto extends BaseResidentDto{
   public  String  phone;

   public List<HeathIconDto> icons;

}
