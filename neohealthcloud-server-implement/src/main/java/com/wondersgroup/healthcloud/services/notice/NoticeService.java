package com.wondersgroup.healthcloud.services.notice;

import com.wondersgroup.healthcloud.jpa.entity.notice.Notice;

import java.util.List;

/**
 * Created by zhaozhenxing on 2016/8/18.
 */
public interface NoticeService {

    Notice findNoticeByid(String id);

    List<Notice> findAllNoticeByArea(String mainArea, String specArea);

    Notice findNoticeByAreaForApp(String mainArea, String specArea);

    Notice saveNotice(Notice notice);
}
