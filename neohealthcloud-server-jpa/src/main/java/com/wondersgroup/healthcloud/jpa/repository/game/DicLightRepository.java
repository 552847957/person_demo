package com.wondersgroup.healthcloud.jpa.repository.game;

import com.wondersgroup.healthcloud.jpa.entity.game.DicLight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zhuchunliu on 2016/10/20.
 */
public interface DicLightRepository extends JpaRepository<DicLight,String> {

    @Query("select count(a) from DicLight a where a.registerid = ?1 and a.delFlag = '0'")
    Integer findByRegisterId(String registerid);

    @Query("select count(a) from DicLight a where a.delFlag = '0'")
    Integer getTotal();
}
