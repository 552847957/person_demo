package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.UserFans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ys on 2016/12/10.
 *
 * @author ys
 */
public interface UserFansRepository extends JpaRepository<UserFans, Integer> {

    //查询我关注的用户数量
    @Query(nativeQuery = true, value = "select count(1) from tb_bbs_fans where fans_uid = ?1 and del_flag = '0'")
    int countAttentNumForUser(String uid);

    @Query(nativeQuery = true, value = "select count(1) from tb_bbs_fans where uid = ?1 and del_flag = '0'")
    int countFansNumForUser(String uid);

    UserFans queryByUidAndFansUid(String uId, String fansUid);

    /**
     * 从filterUids筛选出我关注的用户
     */
    @Query("select t.uid from UserFans t where t.fansUid = ?1 and t.uid in ?2")
    List<String> filterMyAttentUser(String myUid, List<String> filterUids);

    /**
     * 从filterUids筛选出我的粉丝
     */
    @Query("select t.uid from UserFans t where t.uid = ?1 and t.fansUid in ?2")
    List<String> filterMyFans(String myUid, List<String> filterUids);

}
