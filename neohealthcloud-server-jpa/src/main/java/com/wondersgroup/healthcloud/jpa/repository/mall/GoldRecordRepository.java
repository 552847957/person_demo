package com.wondersgroup.healthcloud.jpa.repository.mall;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;

public interface GoldRecordRepository extends JpaRepository<GoldRecord, String> {

	List<GoldRecord> findByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);

	List<GoldRecord> findByUserIdAndTypeOrderByCreateTimeDesc(String userId, int type, Pageable pageable);

}
