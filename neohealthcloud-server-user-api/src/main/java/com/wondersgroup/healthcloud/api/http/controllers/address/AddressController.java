package com.wondersgroup.healthcloud.api.http.controllers.address;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.area.DicArea;
import com.wondersgroup.healthcloud.jpa.repository.area.DicAreaRepository;

@RestController
@RequestMapping("/api")
public class AddressController {
	
	@Autowired
	private DicAreaRepository dicAreaRepository;


	 /**
     * 查询省市区字段表数据
     * @param upperCode
     * @return JsonListResponseEntity<DicArea>
     */
    @RequestMapping(value = "/address/firstAddressInfo", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<DicArea> getFirstAddressInfo(@RequestParam(required = false) String upperCode) {
        JsonListResponseEntity<DicArea> entity = new JsonListResponseEntity<DicArea>();
        if(StringUtils.isEmpty(upperCode)){
            entity.setContent(dicAreaRepository.getAddressListByLevel("1"));
        }else{
            entity.setContent(dicAreaRepository.getAddressListByLevelAndFatherId(upperCode));
        }
        return entity;
    }
	
	
}

