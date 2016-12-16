package com.wondersgroup.healthcloud.jpa.repository.mall;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;

public interface GoldRecordRepository extends JpaRepository<GoldRecord, String> {

	/**
	 * 查询用户最近的一次金币记录
	 * 
	 * @param userId
	 * @return
	 */
	@Query(value = "from GoldRecord where userId = ?1 and createTime = (select max(createTime) from GoldRecord where userId = ?1)")
	GoldRecord findByUserIdRecently(String userId);

	/**
	 * 根据记录类型查询用户某一天的金币记录
	 * 
	 * @param userId
	 * @param type
	 * @param today
	 * @return
	 */
	@Query(value = "from GoldRecord where userId = ?1 and type = ?2 and date_format(createTime,'%Y-%m-%d') = date_format(?3, '%Y-%m-%d')")
	List<GoldRecord> findByUserIdAndTypeAndCreateTime(String userId, int type, Date today);

	Page<GoldRecord> findByUserId(String userId, Pageable pageable);

}
