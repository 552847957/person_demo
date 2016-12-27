package com.wondersgroup.healthcloud.services.bbs.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by ys on 16/08/11.
 *
 */
public class PublishCommentException extends BaseException {

    private PublishCommentException(Integer code, String msg) {
        super(code, msg, null);
    }

    public static PublishCommentException topicWaitVerify(){
        return new PublishCommentException(1041, "话题审核中,审核通过才能回复哦");
    }

    public static PublishCommentException circleDel(){
        return new PublishCommentException(1041, "回复失败,该圈已禁用");
    }

    public static PublishCommentException userBan(){
        return new PublishCommentException(1131, "回复失败,您已被禁言");
    }
}
