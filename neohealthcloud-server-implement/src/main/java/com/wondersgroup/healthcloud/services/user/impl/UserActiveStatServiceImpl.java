package com.wondersgroup.healthcloud.services.user.impl;

import com.wondersgroup.healthcloud.services.user.UserActiveStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by matt on 17/7/6.
 */
@Service("userActiveStatServiceImpl")
public class UserActiveStatServiceImpl implements UserActiveStatService{
    @Autowired
    private JdbcTemplate jt;
    @Override
    public List<Map<String, Object>> queryUserActiveStatList(Map<String, Object> param) {
        String sql=makeSql(param,1);
        List<Map<String,Object>> results = jt.queryForList(sql);
        return results;
    }

    private String makeSql(Map<String, Object> searchParam, int type) {
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        if(type == 2){
            sql.append(" count(*) ");
        }else{
            if(null==searchParam.get("type") || searchParam.get("type").equals("1")){
                sql.append(" * ");
            }else{//累加统计
                sql.append(" st.stat_date as stat_date,")
                        .append("(select sum(s.regname) from stat_jky_useractive as s where s.stat_date<=st.stat_date) as regtotal,")
                        .append("(select sum(s1.identifynum) from stat_jky_useractive as s1 where s1.stat_date<=st.stat_date) as identifytotal");
            }
        }
        sql.append(" from stat_jky_useractive as st where 1=1");
        Iterator it = searchParam.keySet().iterator();
        while (it.hasNext()) {

            String key = (String) it.next();

            if("startTime".equals(key)&&!"".equals(searchParam.get(key))){
                sql.append(" and st.stat_date"+">='"+searchParam.get(key)+"'");
            }if("endTime".equals(key)&&!"".equals(searchParam.get(key))){
                sql.append(" and st.stat_date"+"<='"+searchParam.get(key)+"'");
            }
        }
        sql.append(" order by st.stat_date asc");
        return sql.toString();
    }

    @Override
    public int getCount(Map param) {
        String sql = makeSql(param,2);
        return jt.queryForObject(sql,Integer.class);
    }


}
