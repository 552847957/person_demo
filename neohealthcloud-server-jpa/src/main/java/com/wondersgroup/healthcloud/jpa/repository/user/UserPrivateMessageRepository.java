package com.wondersgroup.healthcloud.jpa.repository.user;

import com.wondersgroup.healthcloud.jpa.entity.user.UserPrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/21/16.
 */
public interface UserPrivateMessageRepository extends JpaRepository<UserPrivateMessage, String> {

    @Query(nativeQuery = true, value = "select m.* from app_tb_user_private_message m inner join(select type, max(create_time) as maxtime from app_tb_user_private_message where uid=?2  and (main_area is null or main_area=?1) group by type) g on m.type=g.type and m.create_time=g.maxtime where uid=?2 and (main_area is null or main_area=?1)")
    List<UserPrivateMessage> findRootMessages(String area, String uid);

    @Query(nativeQuery = true, value = "select * from app_tb_user_private_message where uid=?2 and type=?3 and create_time<FROM_UNIXTIME(?4) and (main_area is null or main_area=?1) order by create_time desc limit 11")
    List<UserPrivateMessage> findTypeMessages(String area, String uid, String type, Long flag);
}
