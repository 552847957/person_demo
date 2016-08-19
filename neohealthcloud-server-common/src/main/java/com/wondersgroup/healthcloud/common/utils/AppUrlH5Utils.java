package com.wondersgroup.healthcloud.common.utils;


/**
 * app h5 url build
 * Created by dukuanxin on 16/08/15.
 */
public class AppUrlH5Utils {


    static String basePath = PropertiesUtils.get("common.basepath.url");

    /**
     * 获取资讯文章h5页面
     * @param articleId
     * @return
     */
    public static String buildNewsArticleView(int articleId) {
        //for_type 用于判断 app进入h5页面的时候，是否需要去请求 检查改h5用户时候收藏过,以及分享信息等
        return basePath + "web/article/new/articleView?from=newsArticle&for_type=article&id="+articleId+"&isToken=1";
    }

    public static String buildDiseaseArticleView(int articleId) {

        return "";
    }

    public static String buildFoodStoreView(int id) {

        return "";
    }
}
