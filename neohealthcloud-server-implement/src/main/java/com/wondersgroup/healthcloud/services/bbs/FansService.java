package com.wondersgroup.healthcloud.services.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Fans;
import com.wondersgroup.healthcloud.services.bbs.dto.AttentDto;
import com.wondersgroup.healthcloud.utils.Page;

import java.util.List;
import java.util.Map;

/**
 * </p>
 * Created by jialing.yao on 2016-8-12.
 */
public interface FansService {

    /**
     * 分页查询关注人列表（我是粉丝，关注别人）
     *
     * @param uid
     * @param page
     * @return
     */
    Page queryAttentListByUid(String uid, Page page);

    /**
     * 分页查询关注人列表（我是粉丝，关注别人）
     *
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<AttentDto> getAttentListByUid(String uid, int pageNo, int pageSize);
    Fans queryByUIdAndFansUid(String uId, String fansUid);
    Fans queryByUIdAndFansUidAndDelFlag(String uId, String fansUid, String delFlag);
    Fans saveFans(Fans fans);
    int countAttentByUid(String uid);
    Boolean isAttentUser(String uid, String targetUid);

    //查顶层粉丝列表
    Page queryFansListByTopUid(String topUid, Page page);

    // 查看关注列表-分页
    Page queryAttentListByTopUid(String topUid, Page page);

    int countFansByTopUid(String topUid);
    List<Map<String,Object>> getFansListByTopUid(String topUid, int pageNo, int pageSize);

    // 查看关注列表
    List<Map<String,Object>> getAttentListByTopUid(String topUid, int pageNo, int pageSize);

    //查点击某个粉丝的粉丝列表
    Page queryFansListByClickUid(String topUid, String clickUid, Page page);
    int countFansByClickUid(String topUid, String clickUid);
    List<Map<String,Object>> getFansListByClickUid(String topUid, String clickUid, int pageNo, int pageSize);

    // 查询点击某个用户的关注列表
    Page queryAttentListByClickUid(String topUid, String clickUid, Page page);
    // 查看点击用户的关注列表
    List<Map<String, Object>> getAttentListByClickUid(String topUid, String clickUid, int pageNo, int pageSize);

    //是否相互关注
    boolean ifAttentEachOther(String otherId, String uId);
}
