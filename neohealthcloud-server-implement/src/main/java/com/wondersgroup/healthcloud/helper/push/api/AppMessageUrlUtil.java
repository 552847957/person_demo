package com.wondersgroup.healthcloud.helper.push.api;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

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
        HTTP("-1", "http", null, null, false),
        SYSTEM("0", "系统消息", null, "/system", false),
        QUESTION("1", "我的咨询", null, "/question", true),
        FAMILY("2", "亲情账户", null, "/family", false),
        ACTIVITY("3", "健康活动", null, "/activity", false);

        public final String id;
        public final String name;
        public final String icon;
        public final String urlPrefix;
        public final Boolean showTitleInRoot;
        private static final Map<String, Type> idTypeMap;

        static {
            ImmutableMap.Builder<String, Type> builder = ImmutableMap.builder();
            for (Type type : values()) {
                builder.put(type.id, type);
            }
            idTypeMap = builder.build();
        }

        Type(String id, String name, String icon, String urlPrefix, Boolean showTitleInRoot) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.urlPrefix = urlPrefix;
            this.showTitleInRoot = showTitleInRoot;
        }

        public static Type getById(String id) {
            return idTypeMap.get(id);
        }
    }

    /**
     * 跳转至实名认证信息页
     *
     * @return
     */
    public static String verificationCallback(String uid, Boolean success,Integer type) {
        return Type.SYSTEM.urlPrefix + "/verification?uid=" + uid + "&success=" + success +"&type="+type;
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
