package com.wondersgroup.healthcloud.common.utils;

/**
 * Created by ys on 16/8/26.
 *
 */
public class NumberUtils {

    private static final java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat(".#");

    /**
     * 格式化数字
     * >10000
     * eq: 11092显示为1.1万
     */
    public static String formatCustom1(int num) {
        String numStr = "";
        if (num < 10000){
            numStr = String.valueOf(num);
        }else {
            double dd = num/10000.0;
            numStr = decimalFormat.format(dd) + "万";
        }
        return numStr;
    }

}
