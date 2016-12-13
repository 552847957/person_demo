package com.wondersgroup.healthcloud.services.bbs;


import com.wondersgroup.healthcloud.services.bbs.criteria.UserSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.AdminVestInfoDto;

import java.util.List;
import java.util.Map;

/**
 * Created by ys on 2016/08/11.
 * 用户 - 圈子 的相关
 */
public interface BbsAdminService {

    Boolean bindAppUser(String adminId, String appUid);

    void cancelBBSAdmin(String mobile);

    void addUpdateAdminVestUser(String adminUid, AdminVestInfoDto vestUser);

    List<AdminVestInfoDto> findAdminVestUsers(String adminUid, int page, int pageSize);

    AdminVestInfoDto getAdminVestInfo(Integer id);

    int countAdminVestNum(String adminUid);
    /**
     * 通过管理员绑定的uid获取关联的app小号uids
     */
    List<String> getAdminVestUidsByAdminUid(String admin_bindUid);

    List<Map<String,Object>> findUserListByCriteria(UserSearchCriteria searchCriteria);

    int countUserByCriteria(UserSearchCriteria searchCriteria);
}
