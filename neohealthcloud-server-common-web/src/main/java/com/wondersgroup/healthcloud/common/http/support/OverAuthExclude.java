package com.wondersgroup.healthcloud.common.http.support;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by nick on 2017/5/3.
 */
public class OverAuthExclude {

    private static List<String> overAuthExcludes = Lists.newArrayList();

    private static final String APPOINTMENT = "/api/reservation";

    private static final String BBS = "/api/bbs";

    static{
        overAuthExcludes.add(APPOINTMENT);
        overAuthExcludes.add(BBS);
    }

    public Boolean isExclude(String request){
        if(!StringUtils.isEmpty(request)){
            for(String overAuthExclude: overAuthExcludes){
                if(request.startsWith(overAuthExclude))
                    return true;
            }
        }
        return false;
    }
}
