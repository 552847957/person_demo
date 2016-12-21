package com.wondersgroup.healthcloud.helper.family;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

/**
 * Created by zhangzhixiu on 15/9/4.
 */
public class FamilyMemberRelation {
    public static String SEX_MAN  = "男";
    public static String SEX_GIRL = "女";
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
        return relationNames[_code];
    }

    public static String getOppositeRelation(String code, String gender) {
        Integer _code = Integer.valueOf(code);
        Integer _gender = Integer.valueOf(gender);
        Preconditions.checkArgument(-1 < _code && _code < 39, "错误的关系代码, 只能为0~5之间的整数");
        Preconditions.checkArgument(_gender == 1 || _gender == 2, "必须确定性别");
//        if (_code == 0 || _code == 3) {
//            return String.valueOf(_code);
//        } else {
//            if (_code == 1 || _code == 2) {
//                return _gender == 1 ? "4" : "5";
//            } else {
//                return _gender == 1 ? "1" : "2";
//            }
//        };
        return getIndexByRelationAndSex(code, _gender);
    }
    
    /**
     * 获取所有关系，分3组
     * @return List<List<String>>
     */
    public static List<List<String>> getMemberFooting(){
        List<List<String>> result = new ArrayList<List<String>>();
        List<String> list = Arrays.asList(relationNames);
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        List<String> list3 = new ArrayList<String>();
        for (int i = 1; i < list.size(); i++) {
            if(i <= 9){
                list1.add(list.get(i));
            }else if(i <= 24){
                list2.add(list.get(i));
            }else{
                list3.add(list.get(i));
            }
        }
        list3.add(relationNames[0]);
        result.add(list1);
        result.add(list2);
        result.add(list3);
        return result;
    }
    
    /**
     * 获取所有关系，分1组
     * @return List<List<String>>
     */
    public static List<Map<String,Object>> getMemberFootings(){
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        for (int i = 1; i < relationNames.length; i++) {
            Map<String,Object> map = new TreeMap<String,Object>();
            map.put("relation", i);
            map.put("relation_name", relationNames[i]);
            result.add(map);
        }
        Map<String,Object> map = new TreeMap<String,Object>();
        map.put("relation", 0);
        map.put("relation_name", relationNames[0]);
        result.add(map);
        return result;
    }
    
    public static String getSexIndex(String sexName){
        return SEX_MAN.equals(sexName) ? "1" : "2";
    }
    
    public static String getSexByRelationAndSex(String relation, String gender){
        Integer sex = gender == null ? null : Integer.parseInt(gender);
//        relation = getIndexByRelationAndSex(relation, sex);
        switch (relation) {
        case "1": return SEX_MAN;
        case "2": return SEX_GIRL;
        case "3": 
            if(sex != null){
                return sex == 2 ? SEX_GIRL : SEX_MAN; 
            }
        case "4": return SEX_MAN;
        case "5": return SEX_GIRL;
        case "6": return SEX_MAN;
        case "7": return SEX_GIRL;
        case "8": return SEX_MAN;
        case "9": return SEX_GIRL;
        case "10": return SEX_MAN;
        case "11": return SEX_GIRL;
        case "12": return SEX_MAN;
        case "13": return SEX_GIRL;
        case "14": return SEX_MAN;
        case "15": return SEX_GIRL;
        case "16": return SEX_MAN;
        case "17": return SEX_GIRL;
        case "18": return SEX_GIRL;
        case "19": return SEX_GIRL;
        case "20": return SEX_MAN;
        case "21": return SEX_MAN;
        case "22": return SEX_MAN;
        case "23": return SEX_GIRL;
        case "24": return SEX_MAN;
        case "25": return SEX_GIRL;
        case "26": return SEX_MAN;
        case "27": return SEX_GIRL;
        case "28": return SEX_MAN;
        case "29": return SEX_GIRL;
        case "30": return SEX_MAN;
        case "31": return SEX_GIRL;
        case "32": return SEX_GIRL;
        case "33": return SEX_MAN;
        case "34": return SEX_MAN;
        case "35": return SEX_GIRL;
        case "36": return SEX_MAN;

        default: return SEX_MAN;
        }
    }
    
    public static String getIndexByRelationAndSex(String relation, Integer sex){
        switch (relation) {
        case "1": return sex == 2 ? "5" : "4"; 
        case "2": return sex == 2 ? "5" : "4";
        case "3": return "3"; 
        case "4": return sex == 2 ? "2" : "1";
        case "5": return sex == 2 ? "2" : "1";
        case "6": return sex == 2 ? "27" : "26";
        case "7": return sex == 2 ? "27" : "26";
        case "8": return sex == 2 ? "29" : "28";
        case "9": return sex == 2 ? "29" : "28";
        case "10": return "24";
        case "11": return "24";
        case "12": return "25";
        case "13": return "25";
        case "14": return sex == 2 ? "23" : "22";
        case "15": return sex == 2 ? "23" : "22";
        case "16": return sex == 2 ? "31" : "30";
        case "17": return sex == 2 ? "31" : "30";
        case "18": return sex == 2 ? "19" : "21";
        case "19": return sex == 2 ? "19" : "21";
        case "20": return sex == 2 ? "18" : "20";
        case "21": return sex == 2 ? "18" : "20";
        case "22": return sex == 2 ? "15" : "14";
        case "23": return sex == 2 ? "15" : "14";
        case "24": return sex == 2 ? "11" : "10";
        case "25": return sex == 2 ? "13" : "12";
        case "26": return sex == 2 ? "7" : "6";
        case "27": return sex == 2 ? "7" : "6";
        case "28": return sex == 2 ? "9" : "8";
        case "29": return sex == 2 ? "9" : "8";
        case "30": return sex == 2 ? "17" : "16";
        case "31": return sex == 2 ? "17" : "16";
        case "32": return sex == 2 ? "34" : "35";
        case "33": return sex == 2 ? "34" : "35";
        case "34": return sex == 2 ? "33" : "32";
        case "35": return sex == 2 ? "33" : "32";
        case "36": return "37";
        case "37": return "36";
        case "38": return "38";

        default: return "0";
        }
    }
}
