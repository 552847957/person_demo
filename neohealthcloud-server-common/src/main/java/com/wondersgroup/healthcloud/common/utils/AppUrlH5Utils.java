package com.wondersgroup.healthcloud.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * app h5 url build Created by dukuanxin on 16/08/15.
 */
@Component
public class AppUrlH5Utils {

    @Value("${h5-web.connection.url}")
    private String basePath;

    @Value("${h5-mall.connection.url}")
    private String mallBasePath;

    @Value("${h5-diabetes.connection.url}")
    private String diabetesBasePath;//糖尿病专区
    //跳医生端下载地址
    public static final String buildSkipToDownLodLink="https://www.wdjky.com/neohealthcloud-app-h5/";
    /**
     * 圈子的话题详情
     */
    public String buildBbsTopicView(int topic_id) {
        return basePath + "/topic/detail?topicId=" + topic_id + "&area=3101";
    }

    /**
     * 获取资讯文章h5页面
     */
    public String buildNewsArticleView(int articleId, String area) {
        // for_type 用于判断 app进入h5页面的时候，是否需要去请求 检查改h5用户时候收藏过,以及分享信息等
        return basePath + "/article/detail?id=" + articleId + "&source=h5&for_type=article&area=" + area;
    }

    public String buildFoodStoreView(int id) {
        return basePath + "/foodstore/detail?id=" + id;
    }

    public String buildBasicUrl(String url) {
        return basePath + url;
    }

    /**
     * 学苑 文章 详情
     */
    public String buildXueYuanArticleView(int articleId) {
        // for_type 用于判断 app进入h5页面的时候，是否需要去请求 检查改h5用户时候收藏过,以及分享信息等
        return basePath + "/article/doctorArticleDetail?from=doctor&for_type=article&isuser=false&id=" + articleId;
    }

    /**
     * 健康档案 - 住院记录
     */
    public String buildHealthRecordZhuyuan(String idc) {
        return basePath + "/healthRecords/operationList?idc=" + idc;
    }

    /**
     * 健康档案 - 就诊报告
     */
    public String buildHealthRecordJiuzhen(String idc) {
        return basePath + "/healthRecords/seedoctorList?idc=" + idc;
    }

    /**
     * 健康档案 - 检验报告
     */
    public String buildHealthRecordJianyan(String idc) {
        return basePath + "/healthRecords/examList?idc=" + idc;
    }

    /**
     * 健康档案 - 市级调阅
     */
    public String buildHealthRecord(String idc) {
        return basePath + "/healthRecords/home?idc=" + idc;
    }

    /**
     * 健康档案 - 用药列表
     */
    public String buildHealthRecordYongyao(String idc) {
        return basePath + "/healthRecords/drugList?idc=" + idc;
    }

    /**
     * 圈子版规
     */
    public String buildBbsVrules() {
        return basePath + "/vrules";
    }

    public Map<String, String> generateLinks(String idc) {
        Map<String, String> result = new HashMap<>();
        result.put("zhuyuan_url", buildHealthRecordZhuyuan(idc));
        result.put("jiuzhen_url", buildHealthRecordJiuzhen(idc));
        result.put("yongyao_url", buildHealthRecordYongyao(idc));
        result.put("jianyan_url", buildHealthRecordJianyan(idc));
        return result;
    }

    public String buildWeiXinScan(String doctorId) {
        return basePath + "/doctorDetails?from=doctor&for_type=weixin&doctorId=" + doctorId;
    }

    /**
     * 计步规则
     */
    public String buildStepRuleUrl() {
        return basePath + "/ruler/stepCountRuler";
    }

    /**
     * 计步帮助
     */
    public String buildStepHelpUrl() {
        return basePath + "/ruler/question";
    }

    /**
     * 计步邀请
     */
    public String buildStepInviteUrl(String userId) {
        return basePath + "/hb/?uid=" + userId;
    }

    /**
     * 商城
     */
    public String buildStepMallUrl(String userId) {
        return mallBasePath + "/?forbidden=true&userId=" + userId;
    }

    /**
     * 医生端 - 糖尿病专区
     */
    public String buildDiabetesZoneUrl(){
        return diabetesBasePath;
    }
    
}
