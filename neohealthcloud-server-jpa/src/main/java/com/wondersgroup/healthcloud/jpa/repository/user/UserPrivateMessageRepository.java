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

    @Query(nativeQuery = true, value = "select m.* from (select a.*,if(@type=type,@n\\:=@n+1,@n\\:=1) as rownum,@type\\:=type,@time\\:=create_time from (select * from app_tb_user_private_message where uid=?2 and del_flag='0' and (main_area is null or main_area=?1) order by type,create_time desc,id) a,(select @n\\:=0) b,(select @type\\:='') c,(select @id\\:='') d ,(select @time\\:='') e) m where rownum=1")
    List<UserPrivateMessage> findRootMessages(String area, String uid);

    @Query(nativeQuery = true, value = "select * from app_tb_user_private_message where uid=?2 and type=?3 and del_flag='0' and create_time<FROM_UNIXTIME(?4) and (main_area is null or main_area=?1) order by create_time desc limit 11")
    List<UserPrivateMessage> findTypeMessages(String area, String uid, String type, Long flag);

    @Query(nativeQuery = true,value = "select * from app_tb_user_private_message where uid=?2 and (main_area is null or main_area=?1) and type =?3 and del_flag='0' order by create_time desc limit 1")
    UserPrivateMessage findLastQuestionMsgByUid(String area, String uid,String type);
}
