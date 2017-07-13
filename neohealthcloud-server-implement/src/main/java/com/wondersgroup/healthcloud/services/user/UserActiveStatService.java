package com.wondersgroup.healthcloud.services.user;

import java.util.List;
import java.util.Map;

/**
 * Created by matt on 17/7/6.
 */
public interface UserActiveStatService {
    /**
     * 分页查询用户统计
     * @param param
     * @return
     */
    List<Map<String,Object>> queryUserActiveStatList(Map<String,Object> param);

    /**
     * 查询总记录数
     * @param param
     * @return
     */
    public int getCount(Map param);
}
