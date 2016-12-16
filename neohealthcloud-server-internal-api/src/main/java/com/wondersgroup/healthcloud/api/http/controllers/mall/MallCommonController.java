package com.wondersgroup.healthcloud.api.http.controllers.mall;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.area.DicArea;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoodsHospital;
import com.wondersgroup.healthcloud.jpa.repository.area.DicAreaRepository;
import com.wondersgroup.healthcloud.jpa.repository.mall.GoodsHospitalRepository;

@RestController
@RequestMapping("/api/mall")
public class MallCommonController {

	@Autowired
	GoodsHospitalRepository goodsHospitalRepository;

	@Autowired
	DicAreaRepository dicAreaRepository;

	@RequestMapping(value = "/area", method = RequestMethod.GET)
	public Object area() {
		JsonResponseEntity<List<DicArea>> responseEntity = new JsonResponseEntity<>();
		List<DicArea> var1 = dicAreaRepository.getAddressListByLevelAndFatherId("310100000000"); // 获取上海市辖区
		List<DicArea> var2 = dicAreaRepository.getAddressListByLevelAndFatherId("310200000000"); // 获取上海市辖区
		var1.addAll(var2);
		responseEntity.setData(var1);
		return responseEntity;
	}

	@RequestMapping(value = "/hospital", method = RequestMethod.GET)
	public Object hospital(String areaCode) {
		JsonResponseEntity<List<GoodsHospital>> responseEntity = new JsonResponseEntity<>();
		int delFlag = 0; // 0:未删除
		List<GoodsHospital> list = goodsHospitalRepository.findByAreaCodeAndDelFlag(areaCode, delFlag);
		responseEntity.setData(list);
		return responseEntity;
	}

	@RequestMapping(value = "/street", method = RequestMethod.GET)
	public Object street(String areaCode) {
		JsonResponseEntity<List<DicArea>> responseEntity = new JsonResponseEntity<>();
		List<DicArea> list = dicAreaRepository.getAddressListByLevelAndFatherId(areaCode);
		responseEntity.setData(list);
		return responseEntity;
	}

}
