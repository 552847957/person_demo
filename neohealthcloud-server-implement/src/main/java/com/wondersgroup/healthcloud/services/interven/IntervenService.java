package com.wondersgroup.healthcloud.services.interven;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2015/9/8.
 */
public interface IntervenService {

    /**
     * 获取需要干预的所有用户信息
     *
     * @param personcards 患者身份证信息集合
     * @param query 查询条件
     * @param type 干预类型 如：10000,20000【逗号间隔】
     * @return
     */
    Integer getIntervenCount(String personcards, String query, String type);

    /**
     * 获取需要干预的所有用户信息
     *
     * @param personcards 患者身份证信息集合
     * @param query 查询条件
     * @param type 干预类型 如：10000,20000【逗号间隔】
     * @return
     */
    List<Map<String,Object>> getInterven(String personcards, String query, String type, Integer pageNo, Integer pageSize);

    /**
     * 获取需要干预的所有用户信息
     *
     * @param personcards 患者身份证信息集合
     * @param query 查询条件
     * @param type 干预类型 如：10000,20000【逗号间隔】
     * @return
     */
    List<Map<String,Object>> getInterven(String personcards, String query, String type);

    /**
     * 获取需要干预的详情
     * @param abnormalids 异常指标集合
     * @return
     */
    List<Map<String,Object>> findAllInterven(String abnormalids);


    /**
     * 医生干预
     * @param abnormalids 异常指标集合
     * @param doctorid 医生主键
     * @param content 内容
     * @return
     */
    Integer updateInterven(String abnormalids, String doctorid, String content);

    /**
     * 被干预人员信息
     * @param abnormalids 异常指标集合
     * @return
     */
    List<Map<String,Object>> getRegisterId(String abnormalids);

    /**
     * 获取首页需要显示的异常数据
     * @param personcards
     * @param pageSize 默认显示条数
     * @return
     */
    List<Map<String,Object>> getIntervenHome(String personcards, String pageSize);

    /**
     * 获取需要干预的问题数目
     * @param personcards
     * @return
     */
    Integer findAllIntervenCount(String personcards);
}
