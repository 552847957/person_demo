package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.BaseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Administrator on 2016/12/12.
 */
public interface BaseInfoRepository extends JpaRepository<BaseInfo, String>, JpaSpecificationExecutor<BaseInfo> {
    @Query("select explainMemo FROM BaseInfo WHERE uid = ?1 and  code = ?2")
    String getExplainMemo(String profession, String profession1);
}
