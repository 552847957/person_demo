package com.wondersgroup.healthcloud.api.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);

    /**
     * 获得文件绝对路径
     *
     * @param request
     * @param fileId
     * @return
     */
    public static String getAbsolutePath(HttpServletRequest request, String fileId) {
        String absolutePath = "";
        try {
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://"
                    + request.getServerName() + ":" + request.getServerPort()
                    + path + "/";
            if (!StringUtils.isEmpty(fileId)) {
                absolutePath = basePath + "file/download/" + fileId;
            } else {
                logger.debug("get file absolute path fail, because file id is empty");
            }
        } catch (NullPointerException ex) {
            logger.debug("get file absolute path fail, because HttpServletRequest is null");
        }
        return StringUtils.trimToNull(absolutePath);
    }

    public static String getImageAbsolutePath(HttpServletRequest request, String fileId) {
        String absolutePath = "";
        try {
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://"
                    + request.getServerName() + ":" + request.getServerPort()
                    + path + "/";
            if (!StringUtils.isEmpty(fileId)) {
                absolutePath = basePath + "images/" + fileId;
            } else {
                logger.debug("get file absolute path fail, because file id is empty");
            }
        } catch (NullPointerException ex) {
            logger.debug("get file absolute path fail, because HttpServletRequest is null");
        }
        return StringUtils.trimToNull(absolutePath);
    }

    public static String getImageAbsoluteFullPath(HttpServletRequest request, String fileId) {
        String absolutePath = "";
        try {
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://"
                    + request.getServerName() + ":" + request.getServerPort()
                    + path + "/";
            if (!StringUtils.isEmpty(fileId)) {
                absolutePath = basePath + "images/" + fileId +"?full=true";
            } else {
                logger.debug("get file absolute path fail, because file id is empty");
            }
        } catch (NullPointerException ex) {
            logger.debug("get file absolute path fail, because HttpServletRequest is null");
        }
        return StringUtils.trimToNull(absolutePath);
    }
}
