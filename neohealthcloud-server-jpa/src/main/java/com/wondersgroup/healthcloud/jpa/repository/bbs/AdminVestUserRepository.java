package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.AdminVestUser;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ys on 2016/08/11
 * 管理员马甲用户
 */
public interface AdminVestUserRepository extends JpaRepository<AdminVestUser, Integer> {

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_admin_vest a where a.admin_uid=?1 and del_flag='0' order by a.create_time desc ")
    List<AdminVestUser> getVestUsersByAdminUid(String admin_uid);
}
