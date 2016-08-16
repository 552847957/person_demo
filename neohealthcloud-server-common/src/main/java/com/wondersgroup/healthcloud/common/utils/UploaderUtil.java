package com.wondersgroup.healthcloud.common.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 * Created by zhuchunliu on 2016/3/9.
 */
public class UploaderUtil {
    private final static String ACCESS_KEY = "kUCOKS5T5i6ygr-5i7MoqimI-EDl5OegkKwkxctw";
    private final static String SECRET_KEY = "q7p2Oyx56blsuh73Y8n-6Geg-y4dUSzKtix_lpPk";
    public final static Integer expires = 3600;//3600秒
    public final static String domain = "img.wdjky.com";
    private static Auth auth = null;

    static {
        auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    }

    private static String[] buckets() {
        BucketManager bucketManager = new BucketManager(auth);
        try {
            return bucketManager.buckets();
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUpToken() {
        return auth.uploadToken("healthcloud2", null, expires, null);
    }

    public static String getUpTokenUEditor(String key) {
        String returnBody = "{\"url\": $(key), \"state\": \"SUCCESS\", \"name\": $(fname),\"size\": \"$(fsize)\",\"w\": \"$(imageInfo.width)\",\"h\": \"$(imageInfo.height)\"}";
        return auth.uploadToken("healthcloud2", key, expires, new StringMap().put("returnBody", returnBody));
    }
}
