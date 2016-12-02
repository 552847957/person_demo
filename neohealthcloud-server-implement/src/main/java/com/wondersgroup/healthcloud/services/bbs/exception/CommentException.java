package com.wondersgroup.healthcloud.services.bbs.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by ys on 16/09/18.
 *
 */
public class CommentException extends BaseException {

    private CommentException(Integer code, String msg) {
        super(code, msg, null);
    }

    public static CommentException NotExistForReport(){
        return new CommentException(1042, "举报失败,评论无效");
    }

}
