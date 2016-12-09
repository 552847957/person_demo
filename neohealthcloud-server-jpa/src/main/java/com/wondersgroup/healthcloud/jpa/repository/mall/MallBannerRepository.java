package com.wondersgroup.healthcloud.jpa.repository.mall;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.mall.MallBanner;

public interface MallBannerRepository extends JpaRepository<MallBanner, String> {

	MallBanner findByGoodsId(Integer goodsId);

}
