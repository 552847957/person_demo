package com.wondersgroup.healthcloud.helper.push.api;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/21/16.
 */
public class AppMessageUrlUtil {

    public enum Type {
        HTTP("-1", "http", null, null),
        SYSTEM("0", "系统消息", null, "/system"),
        QUESTION("1", "轻问诊", null, "/question"),
        FAMILY("2", "亲情账户", null, "/family"),
        ACTIVITY("3", "健康活动", null, "/activity");

        public final String type;
        public final String name;
        public final String icon;
        public final String urlPrefix;

        Type(String type, String name, String icon, String urlPrefix) {
            this.type = type;
            this.name = name;
            this.icon = icon;
            this.urlPrefix = urlPrefix;
        }
    }

    /**
     * 跳转至实名认证信息页
     *
     * @return
     */
    public static String verificationCallback() {
        return Type.SYSTEM.urlPrefix + "/verification";
    }

    /**
     * 跳转至亲情账户邀请页
     *
     * @return
     */
    public static String familyInvitation() {
        return Type.FAMILY.urlPrefix + "/invitation";
    }

    /**
     * 跳转至轻问诊特定页
     *
     * @param id 问题id
     * @return
     */
    public static String question(String id) {
        return Type.QUESTION.urlPrefix + "?id=" + id;
    }

    /**
     * 跳转至活动特定页
     *
     * @param id 活动id
     * @return
     */
    public static String activity(String id) {
        return Type.ACTIVITY.urlPrefix + "?id=" + id;
    }
}
