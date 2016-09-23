package com.wondersgroup.healthcloud.services.interven.impl;

import com.wondersgroup.healthcloud.services.interven.IntervenService;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2015/9/8.
 */
@Service("intervenServiceImpl")

public class IntervenServiceImpl implements IntervenService {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jt;

    /**
     * 获取需要干预的所有用户信息
     *
     * @param personcards 患者身份证信息集合
     * @param query 查询条件
     * @param type 干预类型 如：10000,20000【逗号间隔】
     * @return
     */
    @Override
    public Integer getIntervenCount(String personcards, String query, String type) {
        String sql = "select count(distinct personcard) as total" +
                " from fam_intervention fi where fi.personcard in ('"+personcards.replace(",","','")+"') and fi.is_deal = '0'and fi.remind_date >= '"+DateFormatter.dateFormat(new Date())+"'";
        if(!StringUtils.isEmpty(query)){
            sql += " and (fi.name like '%"+query+"%' or fi.personcard like '%"+query+"%')";
        }
        if(!StringUtils.isEmpty(type)){
            sql += " and fi.type in ('"+type.replace(",","','")+"')";
        }
        List<Map<String,Object>> list = getJt().queryForList(sql);
        return null !=list && 0 == list.size() && null != list.get(0).get("total")?0:Integer.parseInt(list.get(0).get("total").toString()) ;
    }

    /**
     * 获取需要干预的所有用户信息
     *
     * @param personcards 患者身份证信息集合
     * @param query 查询条件
     * @param type 干预类型 如：10000,20000【逗号间隔】
     * @return
     */
    public List<Map<String, Object>> getInterven(String personcards, String query, String type, Integer pageNo, Integer pageSize) {
        String sql = "select fam.*, info.gender,info.birthday,info.regmobilephone,info.registerid from " +
                " (select name,personcard,MAX(warn_date) as warn_date, " +
                " GROUP_CONCAT(abnormalid Separator ',') as abnormalid,GROUP_CONCAT(DISTINCT dic.exc_name Separator ',') as exc_name" +
                " from fam_intervention fi left JOIN fam_abnormal_dic dic on fi.type = dic.abnormal_id  "+
                " where fi.personcard in ('"+personcards.replace(",","','")+"') and fi.remind_date >= '"+DateFormatter.dateFormat(new Date())+"' ";
        if(!StringUtils.isEmpty(query)){
            sql += " and (fi.name like '%"+query+"%' or fi.personcard like '%"+query+"%')";
        }
        if(!StringUtils.isEmpty(type)){
            sql += " and fi.type in ('"+type.replace(",","','")+"')";
        }
        sql += " and fi.del_flag = '0' and fi.is_deal = '0'";
        sql += " GROUP BY name,personcard" +
                " order by  remind_date desc limit "+(pageNo-1)*pageSize+","+pageSize+")fam " +
                " LEFT JOIN abnormal_person_info2 abnormal on fam.personcard = abnormal.personcard_no\n" +
                " LEFT JOIN app_tb_register_info info ON abnormal.personcard_no = info.personcard";
        return getJt().queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getInterven(String personcards, String query, String type) {
        String sql = "select fi.name,fi.personcard,fi.warn_date, info.gender,info.birthday,info.regmobilephone,info.registerid,dic.exc_name" +
                " from fam_intervention fi  left JOIN fam_abnormal_dic dic on fi.type = dic.abnormal_id " +
                " LEFT JOIN abnormal_person_info2 abnormal on fi.personcard = abnormal.personcard_no" +
                " LEFT JOIN app_tb_register_info info ON abnormal.personcard_no = info.personcard" +
                " where  fi.personcard in ('"+personcards.replace(",","','")+"') ";
        if(!StringUtils.isEmpty(query)){
            sql += " and (fi.name like '%"+query+"%' or fi.personcard like '%"+query+"%')";
        }
        if(!StringUtils.isEmpty(type)){
            sql += " and fi.type in ('"+type.replace(",","','")+"')";
        }
        sql += " and fi.del_flag = '0' and fi.is_deal = '0' and fi.remind_date >= '"+ DateFormatter.dateFormat(new Date())+"'";
        return getJt().queryForList(sql);
    }

    /**
     *
     * @param abnormalids 异常指标集合
     * @return
     */
    @Override
    public List<Map<String, Object>> findAllInterven(String abnormalids) {
        String sql = "select inter.name,inter.abnormalid,inter.type,inter.remind_date," +
                " detail.check_result,detail.check_date,dic.exc_name,dic.memo" +
                " from fam_intervention inter join fam_abnormal_detail detail on inter.abnormalid = detail.abnormalid" +
                " left join fam_abnormal_dic dic on inter.type = dic.abnormal_id where inter.abnormalid in ('"+abnormalids.replace(",", "','")+"')" +
                " order by type asc ,inter.abnormalid desc ";
        return getJt().queryForList(sql);
    }

    /**
     *
     * @param abnormalids 异常指标集合
     * @param doctorid 医生主键
     * @param content 内容
     */
    @Override
    public Integer updateInterven(String abnormalids, String doctorid, String content) {
        String sql = "update fam_intervention set is_deal = 1,update_by = '"+doctorid+"',message = '"+content+"', " +
                " deal_date = '"+ DateFormatter.dateTimeFormat(new Date())+"', update_date='"+DateFormatter.dateTimeFormat(new Date())+"'  where abnormalid in ('"+abnormalids.replace(",", "','")+"')";
        return getJt().update(sql);
    }

    /**
     *
     * @param abnormalids 异常指标集合
     * @return
     */
    @Override
    public List<Map<String, Object>> getRegisterId(String abnormalids) {
        String sql = "select DISTINCT info.registerid,inter.name from fam_intervention inter " +
                " LEFT JOIN abnormal_person_info2 abnormal on inter.personcard = abnormal.personcard_no" +
                " LEFT JOIN app_tb_register_info info ON abnormal.personcard_no = info.personcard " +
                " where inter.abnormalid in ('"+abnormalids.replace(",", "','")+"')";

        return getJt().queryForList(sql);
    }

    /**
     *
     * @param personcards
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getIntervenHome(String personcards,String pageSize) {
        String sql = "select fi.name,fi.warn_date, dic.exc_name  \n" +
                " from fam_intervention fi  left JOIN fam_abnormal_dic dic on fi.type = dic.abnormal_id \n" +
                " where fi.personcard in ('"+personcards.replace(",","','")+"') " +
                " and fi.del_flag = '0' and fi.is_deal = '0' and fi.remind_date >= '"+DateFormatter.dateFormat(new Date())+"'"+
                " order by fi.warn_date desc limit "+pageSize;
        return getJt().queryForList(sql);
    }

    @Override
    public Integer findAllIntervenCount(String personcards) {

        String sql = "select count(DISTINCT personcard,name) total  from fam_intervention "+
                " where personcard in ('"+personcards.replace(",","','")+"') and is_deal='0' and del_flag='0' and remind_date >= '"+DateFormatter.dateFormat(new Date())+"'";
        List<Map<String, Object>> list = getJt().queryForList(sql);

        return null == list || 0 == list.size() || null == list.get(0).get("total")?0:Integer.parseInt(list.get(0).get("total").toString());
    }

    /**
     * 获取jdbc template
     *
     * @return
     */
    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }
}
