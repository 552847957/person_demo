package com.wondersgroup.healthcloud.services.yyService;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by ys on 2016/5/18.
 */
public interface VisitUserService {

    public JsonNode postRequest(String personcard, String url, String[] parm);

    public JsonNode postRequest(String url, String[] parm);

    public String[] getRequestHeaderByUid(String uid, Boolean get_real_data);

}
