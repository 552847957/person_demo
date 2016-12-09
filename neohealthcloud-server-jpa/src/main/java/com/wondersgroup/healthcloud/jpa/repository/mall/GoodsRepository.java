package com.wondersgroup.healthcloud.jpa.repository.mall;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;

public interface GoodsRepository extends JpaRepository<Goods, Integer> {

	Page<Goods> findAll(Specification<Goods> specification, Pageable pageable);

	Page<Goods> findByStatus(Integer status, Pageable pageable);

}
