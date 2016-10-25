package com.wondersgroup.healthcloud.services.game.impl;

import com.wondersgroup.healthcloud.services.game.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/10/20.
 */
@Service("lightService")
public class LightServiceImpl implements LightService{

    @Autowired
    private JdbcTemplate jt;
    @Override
    public List<Map<String, Object>> findAreaByParentCode(String code) {
        String sql = null;
        if(code.equals("310000000000")){//统计上海市
            sql = " select code,explain_memo as name from t_dic_area where upper_code in(\n" +
                    " select code from t_dic_area where upper_code = '"+code+"'" +
                    " ) and del_flag = '0'";
        }else{
            sql = "select code,explain_memo as name from t_dic_area " +
                    " where upper_code = '"+code+"' and del_flag = '0'";
        }

        return jt.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getDicLight(String registerid) {
        String sql = " select light.area_code , area.explain_memo as streetName , parent.explain_memo as areaName  from app_tb_area_light light \n" +
                " left join t_dic_area area on light.area_code = area.code\n" +
                " LEFT JOIN  t_dic_area parent on area.upper_code = parent.code" +
                " where light.del_flag = '0' and light.registerid = '"+registerid+"'" +
                " order by light.create_date desc";
        return jt.queryForList(sql);
    }

    @Override
    public Map<String, Object> getRecentDicLight(String registerid) {
        String sql = " select area.explain_memo as streetName , parent.explain_memo as areaName  from app_tb_area_light light \n" +
                " left join t_dic_area area on light.area_code = area.code\n" +
                " LEFT JOIN  t_dic_area parent on area.upper_code = parent.code" +
                " where light.del_flag = '0' and light.registerid = '"+registerid+"'" +
                " order by light.create_date desc limit 1";
        List<Map<String,Object>> list = jt.queryForList(sql);
        return 0 == list.size()?null : list.get(0);
    }

    @Override
    public List<Map<String, Object>> statistic(String code) {

        String sql = null;
        if(code.equals("310000000000")){//统计上海市
            sql = "select code," +
                    " (select count(1)+1 from app_tb_area_light light where light.del_flag = '0' AND light.area_code in " +
                    " (select code from t_dic_area where upper_code = area.code)) as count" +
                    " from t_dic_area area where upper_code in" +
                    "  (select code from t_dic_area where upper_code = '"+code+"') and del_flag = '0'";
        }else{
            sql = "select code,\n" +
                    " (select count(1)+1 from app_tb_area_light light where light.del_flag = '0' AND light.area_code in \n" +
                    " (select code from t_dic_area where upper_code = area.code)) as count\n" +
                    " from t_dic_area area where upper_code = '"+code+"' and del_flag = '0'";
        }
        return jt.queryForList(sql);
    }
}
