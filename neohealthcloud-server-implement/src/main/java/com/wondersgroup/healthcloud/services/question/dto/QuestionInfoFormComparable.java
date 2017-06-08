package com.wondersgroup.healthcloud.services.question.dto;

import java.util.Comparator;

/**
 * Created by xianglinhai on 2016/12/23.
 */
public class QuestionInfoFormComparable implements Comparator<QuestionInfoFormNew> {

    // 对象的排序方式[true 升、false 降]
    public static boolean sortASC = true;

    @Override
    public int compare(QuestionInfoFormNew o1, QuestionInfoFormNew o2) {
       int result = 0;
        if(sortASC){
            result =   o1.sortDate.compareTo(o2.sortDate);
        }else{
            result = - o1.sortDate.compareTo(o2.sortDate);
        }
        return result;
    }


}
