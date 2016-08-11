package com.wondersgroup.healthcloud.helper.family;

import com.google.common.base.Preconditions;

/**
 * Created by zhangzhixiu on 15/9/4.
 */
public class FamilyMemberRelation {
    private static final String[] relationNames = {"其他", "爸爸", "妈妈", "爱人", "儿子", "女儿"};

    public static Boolean isOther(String code) {
        return "0".equals(code);
    }

    public static String getName(String code) {
        Integer _code = Integer.valueOf(code);
        Preconditions.checkArgument(-1 < _code && _code < 6, "错误的关系代码, 只能为0~5之间的整数");
        return relationNames[_code];
    }

    public static String getName(String code, String defaultName) {
        Integer _code = Integer.valueOf(code);
        Preconditions.checkArgument(-1 < _code && _code < 6, "错误的关系代码, 只能为0~5之间的整数");
        return _code == 0 ? defaultName : relationNames[_code];
    }

    public static String getOppositeRelation(String code, String gender) {
        Integer _code = Integer.valueOf(code);
        Integer _gender = Integer.valueOf(gender);
        Preconditions.checkArgument(-1 < _code && _code < 6, "错误的关系代码, 只能为0~5之间的整数");
        Preconditions.checkArgument(_gender == 1 || _gender == 2, "必须确定性别");
        if (_code == 0 || _code == 3) {
            return String.valueOf(_code);
        } else {
            if (_code == 1 || _code == 2) {
                return _gender == 1 ? "4" : "5";
            } else {
                return _gender == 1 ? "1" : "2";
            }
        }
    }
}
