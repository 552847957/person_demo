package com.wondersgroup.healthcloud.common.http.support;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by nick on 2017/5/3.
 */
public class OverAuthExclude {

    private static List<String> overAuthExcludes = Lists.newArrayList();
    private static List<String> overAuthExcludesForDoctor = Lists.newArrayList();

    //==== User Begin
    private static final String APPOINTMENT = "/api/reservation";
    private static final String BBS = "/api/bbs";
    private static final String FAMILY = "/api/family";
    private static final String USER = "/api/user";
    private static final String ASSESSMENT = "/api/assessment";
    private static final String TUBERELATION = "/api/tuberelation";
    //==== User End

    //==== Doctor Begin
    private static final String USERINFO="/api/measure/userInfo";
    private static final String GROUP="/api/group/";
    private static final String HEATHUSERINFO="/api/measure/heathUserInfo";
    private static final String REPORT_FOLLOW="/api/report/follow";
    private static final String LAST_WEEK_HISTORY = "/api/measure/lastWeekHistory";
    private static final String USER_MONITOR = "/api/userMonitor/findUserMonitor";
    private static final String REPORT = "/api/report/follow";
    private static final String SCREENING = "/api/report/screening";
    private static final String MEASUREHISTORY = "/api/measure/getMeasureHistoryByDate";
    //==== Doctor End

    // Init User
    static {
        overAuthExcludes.add(APPOINTMENT);
        overAuthExcludes.add(BBS);
        overAuthExcludes.add(FAMILY);
        overAuthExcludes.add(USER);
        overAuthExcludes.add(ASSESSMENT);
        overAuthExcludes.add(LAST_WEEK_HISTORY);
        overAuthExcludes.add(USER_MONITOR);
        overAuthExcludes.add(REPORT);

    }

    // Init Doctor
    static {
        overAuthExcludesForDoctor.add(USERINFO);
        overAuthExcludesForDoctor.add(GROUP);
        overAuthExcludesForDoctor.add(HEATHUSERINFO);
        overAuthExcludesForDoctor.add(REPORT_FOLLOW);
        overAuthExcludesForDoctor.add(LAST_WEEK_HISTORY);
        overAuthExcludesForDoctor.add(USER_MONITOR);
        overAuthExcludesForDoctor.add(REPORT);
        overAuthExcludesForDoctor.add(SCREENING);
        overAuthExcludesForDoctor.add(MEASUREHISTORY);
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
    
    public Boolean isExcludeForDoctor(String request){
        if(!StringUtils.isEmpty(request)){
            for(String overAuthExclude: overAuthExcludesForDoctor){
                if(request.startsWith(overAuthExclude))
                    return true;
            }
        }
        return false;
    }
}
