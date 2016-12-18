package com.wondersgroup.healthcloud.jpa.repository.mall;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;

public interface GoodsRepository extends JpaRepository<Goods, Integer> {

	Page<Goods> findAll(Specification<Goods> specification, Pageable pageable);

	Page<Goods> findByStatus(Integer status, Pageable pageable);

	@Query(nativeQuery = true, value = "select * from goods_tb where status = ?1 and to_days(now()) > to_days(end_time)")
	List<Goods> findByStatusAndEndTime(Integer status);

}
