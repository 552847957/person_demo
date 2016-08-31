package com.wondersgroup.healthcloud.common.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * app h5 url build
 * Created by dukuanxin on 16/08/15.
 */
@Component
public class AppUrlH5Utils {

    @Value("${h5-web.connection.url}")
    private String basePath;

    /**
     * 获取资讯文章h5页面
     * @param articleId
     * @return
     */
    public String buildNewsArticleView(int articleId) {
        //for_type 用于判断 app进入h5页面的时候，是否需要去请求 检查改h5用户时候收藏过,以及分享信息等
        return basePath + "/article/detail?id="+articleId+"&source=h5&for_type=article";
    }

    public String buildDiseaseArticleView(int articleId) {
        return "";
    }

    public String buildFoodStoreView(int id) {

        return basePath + "/foodstore/detail?id="+id;
    }

    public String buildBasicUrl (String url) {
        return basePath + url;
    }

    /**
     * 学苑 文章 详情
     */
    public String buildXueYuanArticleView(int articleId) {
        //for_type 用于判断 app进入h5页面的时候，是否需要去请求 检查改h5用户时候收藏过,以及分享信息等
        return basePath + "/web/article/xueyuan/articleView?from=doctor&for_type=article&id="+articleId;
    }

    /**
     * 健康档案 - 住院记录
     */
    public String buildHealthRecordZhuyuan(String idc){
        return basePath + "/healthRecords/operationList?idc="+idc;
    }

    /**
     * 健康档案 - 就诊报告
     */
    public String buildHealthRecordJiuzhen(String idc){
        return basePath + "/healthRecords/seedoctorList?idc="+idc;
    }


    /**
     * 健康档案 - 检验报告
     */
    public String buildHealthRecordJianyan(String idc){
        return basePath + "/healthRecords/examList?idc="+idc;
    }


    /**
     * 健康档案 - 用药列表
     */
    public String buildHealthRecordYongyao(String idc){
        return basePath + "/healthRecords/drugList?idc="+idc;
    }

}
