package com.wondersgroup.healthcloud.helper.family;

import com.google.common.base.Preconditions;

/**
 * Created by zhangzhixiu on 15/9/4.
 */
public class FamilyMemberRelation {
    private static final String[] relationNames = {"其他", "爸爸", "妈妈", "爱人", "儿子", "女儿"
        ,"爷爷","奶奶","外公","外婆","岳父","岳母","公公","婆婆","叔叔" ,"姑姑" ,"舅舅" ,"舅妈"
        ,"姐姐" ,"妹妹" ,"哥哥" ,"弟弟" ,"侄子" ,"侄女" ,"女婿" ,"儿媳妇" ,"孙子" ,"孙女" ,"外孙"
        ,"外孙女" ,"外甥" ,"外甥女" ,"干爹" ,"干妈" ,"干女儿" ,"干儿子" ,"男朋友" ,"女朋友" ,"朋友"};

    public static Boolean isOther(String code) {
        return "0".equals(code);
    }

    public static String getName(String code) {
        Integer _code = Integer.valueOf(code);
        Preconditions.checkArgument(-1 < _code && _code < 39, "错误的关系代码, 只能为0~5之间的整数");
        return relationNames[_code];
    }

    public static String getName(String code, String defaultName) {
        Integer _code = Integer.valueOf(code);
        Preconditions.checkArgument(-1 < _code && _code < 39, "错误的关系代码, 只能为0~5之间的整数");
        return _code == 0 ? defaultName : relationNames[_code];
    }

    public static String getOppositeRelation(String code, String gender) {
        Integer _code = Integer.valueOf(code);
        Integer _gender = Integer.valueOf(gender);
        Preconditions.checkArgument(-1 < _code && _code < 39, "错误的关系代码, 只能为0~5之间的整数");
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
