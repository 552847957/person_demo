package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.UserCircle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by limenghua on 2016/8/15.
 *
 * @author limenghua
 */
public interface UserCircleRepository extends JpaRepository<UserCircle, Integer> {

    UserCircle queryByUIdAndCircleIdAndDelFlag(String uId, Integer circleId, String delFlag);

    UserCircle queryByUIdAndCircleId(String uId, Integer circleId);

    @Query(nativeQuery = true, value = "select count(1) from tb_bbs_user_circle t where t.circle_id = :circleId and del_flag = :delFlag")
    int getAttentCount(@Param("circleId") Integer circleId, @Param("delFlag") String delFlag);
}
