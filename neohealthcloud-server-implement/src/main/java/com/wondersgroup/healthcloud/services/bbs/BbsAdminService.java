package com.wondersgroup.healthcloud.services.bbs;



import java.util.List;
import java.util.Map;

/**
 * Created by ys on 2016/08/11.
 * 用户 - 圈子 的相关
 */
public interface BbsAdminService {

    Boolean bindAppUser(String adminId, String appUid);

    void cancelBBSAdmin(String mobile);

    /**
     * 通过管理员绑定的uid获取关联的app小号uids
     */
    List<String> getAssociationUidsByAdminId(String admin_bindUid);

}
