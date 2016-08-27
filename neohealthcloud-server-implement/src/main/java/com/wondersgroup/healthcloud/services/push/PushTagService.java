package com.wondersgroup.healthcloud.services.push;

import com.wondersgroup.healthcloud.jpa.entity.push.PushTag;

import java.util.List;

/**
 * Created by zhuchunliu on 2016/8/26.
 */
public interface PushTagService {
    public Boolean bindTag(String tagname, String uids);

    List<PushTag> findAll();
}


