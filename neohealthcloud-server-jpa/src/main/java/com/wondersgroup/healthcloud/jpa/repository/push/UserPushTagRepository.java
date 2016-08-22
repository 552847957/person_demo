package com.wondersgroup.healthcloud.jpa.repository.push;

import com.wondersgroup.healthcloud.jpa.entity.push.UserPushTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

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
 * Created by zhangzhixiu on 8/17/16.
 */
public interface UserPushTagRepository extends JpaRepository<UserPushTag, Integer> {

    @Query("select upt from UserPushTag upt where upt.uid=?1")
    List<UserPushTag> getByUid(String uid);

    @Query("select upt.tagid from UserPushTag upt where upt.uid=?1")
    Set<String> getIdsByUid(String uid);

    @Query("select upt from UserPushTag upt where upt.tagid=?1")
    List<UserPushTag> getByTag(Integer tag);

    @Query("select count(0) from UserPushTag upt where upt.tagid=?1")
    Integer tagCount(Integer tag);

    @Modifying
    @Query("delete from UserPushTag upt where upt.tagid=?1")
    Integer deleteByTag(Integer tag);
}
