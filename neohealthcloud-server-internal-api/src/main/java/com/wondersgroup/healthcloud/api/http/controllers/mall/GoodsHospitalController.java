package com.wondersgroup.healthcloud.api.http.controllers.mall;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.administrative.Area;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoodsHospital;
import com.wondersgroup.healthcloud.jpa.repository.administrative.AreaRepository;
import com.wondersgroup.healthcloud.jpa.repository.mall.GoodsHospitalRepository;

@RestController
@RequestMapping("/api/goods")
public class GoodsHospitalController {

	@Autowired
	AreaRepository areaRepository;

	@Autowired
	GoodsHospitalRepository goodsHospitalRepository;

	@RequestMapping(value = "/area", method = RequestMethod.GET)
	public Object area() {
		JsonResponseEntity<List<Area>> responseEntity = new JsonResponseEntity<>();
		List<Area> list = areaRepository.findAll();
		responseEntity.setData(list);
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

}
