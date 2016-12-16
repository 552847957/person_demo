package com.wondersgroup.healthcloud.jpa.repository.mall;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.mall.GoodsHospital;

public interface GoodsHospitalRepository extends JpaRepository<GoodsHospital, String> {

	List<GoodsHospital> findByAreaCodeAndDelFlag(String areaCode, Integer delFlag);

}
