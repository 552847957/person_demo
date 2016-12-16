package com.wondersgroup.healthcloud.services.bbs;

import com.wondersgroup.healthcloud.utils.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by jialing.yao on 2016-8-17.
 *
 */
public interface BbsSysMsgService {

    //动态消息列表、系统消息列表
    Page queryMsgListByUid(String uid, Page page);

    int countMsgByUid(String uid);

    List<Map<String,Object>> getMsgListByUid(String uid, int pageNo, int pageSize);

    /**
     * 消息中心接口-查找最近一条未读动态消息
     * @param uid
     * @return row
     */
    Map<String,Object> findOneDynamicMessageByUid(String uid);
    /**
     * 消息中心接口-查找最近一条未读系统消息
     * @param uid
     * @return row
     */
    Map<String,Object> findOneSysMessageByUid(String uid);

    /**
     * 统计未读消息
     * @param uid
     * @return
     */
    int countOfUnReadMessages(String uid);

    //设置全部未读消息为已读
    void setAllRead(String uid);

}
