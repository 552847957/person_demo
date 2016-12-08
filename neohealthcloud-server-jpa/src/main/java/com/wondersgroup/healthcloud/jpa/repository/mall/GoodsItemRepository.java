package com.wondersgroup.healthcloud.jpa.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.mall.GoodsItem;

public interface GoodsItemRepository extends JpaRepository<GoodsItem, String> {

	List<GoodsItem> findByGoodsId(Integer goodsId);

	Page<GoodsItem> findAll(Specification<GoodsItem> specification, Pageable pageable);

}
