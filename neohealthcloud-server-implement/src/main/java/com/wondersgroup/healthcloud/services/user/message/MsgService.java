package com.wondersgroup.healthcloud.services.user.message;

import com.wondersgroup.healthcloud.utils.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by jialing.yao on 2016-12-12.
 */
public interface MsgService {

    //消息列表分页
    Page queryMsgListByUid(String uid, Page page);
    //统计消息
    int countMsgByUid(String uid);
    //消息列表
    List<Map<String,Object>> getMsgListByUid(String uid, int pageNo, int pageSize);

    /**
     * 消息中心接口-查找最近一条未读动态消息
     */
    Map<String,Object> findOneMessageByUid(String uid);

    /**
     * 统计未读消息
     */
    int countOfUnReadMessages(String uid);

    //批量设为已读
    void setRead(List<Integer> ids);
    //设置全部未读消息为已读
    void setAllRead(String uid);
    
    //根据uid查 当天有发送过这个type消息
    int getCountByDate(String uid, String memberId, int type);

    int countMsgByUidAndType(String uid, String typeCode);

    int countOfUnReadMessagesByUidType(String uid, String typeCode);

    Map<String,Object> findLastMessageByUidType(String uid, String typeCode);
}
