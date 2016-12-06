package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.UserBanLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by ys on 2016/8/21
 *
 * @author ys
 */
public interface UserBanLogRepository extends JpaRepository<UserBanLog, Integer> {

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_user_ban_log a where a.uid=?1 order by a.create_time desc limit 1")
    UserBanLog findLastLogForUser(String uid);
}
