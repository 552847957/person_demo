package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.TubeRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by zhuchunliu on 2016/12/14.
 */
public interface TubeRelationRepository extends JpaRepository<TubeRelation, String>, JpaSpecificationExecutor<TubeRelation> {
    @Query("select a from TubeRelation a where registerid = ?1 and delFlag = '0'")
    TubeRelation getRelationByRegisterId(String registerid);

    @Transactional
    @Modifying
    @Query("delete from TubeRelation where registerid = ?1")
    void deleteRelationByRegisterId(String registerid);
}
