package com.wondersgroup.healthcloud.services.game;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/10/20.
 */
public interface LightService {
    List<Map<String, Object>> findAreaByParentCode(String code);

    List<Map<String,Object>> getDicLight(String registerid);

    Map<String,Object> getRecentDicLight(String registerid);

    List<Map<String,Object>> statistic(String code);
}
