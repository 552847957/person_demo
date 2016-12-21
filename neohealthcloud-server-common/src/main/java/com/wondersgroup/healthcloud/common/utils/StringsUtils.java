package com.wondersgroup.healthcloud.common.utils;


import org.apache.commons.lang3.StringUtils;

/**
 *
 * Created by ys on 16/12/18.
 */
public class StringsUtils {

    public static String subString(String str, int length, String padStr) {
        if (StringUtils.length(str) <= length || length <= 0){
            return str;
        }
        String subStr = StringUtils.substring(str, 0, length);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(subStr.charAt(length-1));
        if (block == Character.UnicodeBlock.HIGH_SURROGATES){
            subStr = subString(subStr, length-1, "");
        }
        return subStr + padStr;
    }

    /**
     * 截取字符串(处理带特殊表情的字符串)
     */
    public static String subString(String str, int length) {
        return subString(str, length, "");
    }

}
