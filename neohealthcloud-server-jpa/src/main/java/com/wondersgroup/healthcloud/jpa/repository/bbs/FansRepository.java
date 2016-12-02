package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Fans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by limenghua on 2016/8/17.
 *
 * @author limenghua
 */
public interface FansRepository extends JpaRepository<Fans, Integer> {
    @Query(nativeQuery = true, value = "select count(1) from tb_bbs_fans where fans_uid = :uId and del_flag = '0'")
    int getAttentCount(@Param("uId") String uId);

    /**
     * 查看点击用户的关注总数，如果关注中包含登录用户的id，排除
     *
     * @param uId
     * @param clickUid
     * @return
     */
    @Query(nativeQuery = true, value = "select count(1) from tb_bbs_fans where fans_uid = :clickUid and uid <> :uId and del_flag = '0'")
    int getAttentCountByUidAndClickUid(@Param("uId") String uId, @Param("clickUid") String clickUid);

    Fans queryByUIdAndFansUid(String uId, String fansUid);

    Fans queryByUIdAndFansUidAndDelFlag(String uId, String fansUid, String delFlag);
}
