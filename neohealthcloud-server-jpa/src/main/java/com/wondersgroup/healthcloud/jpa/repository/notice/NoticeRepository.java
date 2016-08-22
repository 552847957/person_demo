package com.wondersgroup.healthcloud.jpa.repository.notice;

import com.wondersgroup.healthcloud.jpa.entity.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zhaozhenxing on 2016/8/18.
 */
public interface NoticeRepository extends JpaRepository<Notice, String> {
    @Query(value = "select a from Notice a where a.delFlag = '0' and a.id = ?1")
    Notice findNoticeByid(String id);
}
