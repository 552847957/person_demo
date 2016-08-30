package com.wondersgroup.healthcloud.helper.medicincase;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by qiujun on 2015/9/5.
 */
public class MedicinCaseConstant {

    public static final String ADD_NEW_CASE = "添加了病历";

    public static final String UPDATE_CASE_RECORD = "修改了病历记录";

    public static final String UPDATE_CASE_BASIC_INFO = "修改了病历基本信息";

    public static final String ADD_NEW_CASE_TYPE = "1";

    public static final String UPDATE_CASE_RECORD_TYPE = "2";

    public static final String UPDATE_CASE_BASIC_INFO_TYPE = "3";

    public static Map<String, String> parm = Maps.newHashMap();

    static{
        parm.put(ADD_NEW_CASE_TYPE,ADD_NEW_CASE);
        parm.put(UPDATE_CASE_RECORD_TYPE,UPDATE_CASE_RECORD);
        parm.put(UPDATE_CASE_BASIC_INFO_TYPE,UPDATE_CASE_BASIC_INFO);
    }

    public static String getActionDesc(String actionType){
        return parm.get(actionType);
    }
}
