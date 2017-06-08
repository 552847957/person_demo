package com.wondersgroup.healthcloud.services.question.dto;

import java.util.Comparator;

/**
 * Created by xianglinhai on 2016/12/23.
 */
public class QuestionInfoFormIsReadComparable implements Comparator<QuestionInfoForm> {

    // 对象的排序方式[true 升、false 降]
    public static boolean sortASC = true;

    @Override
    public int compare(QuestionInfoForm o1, QuestionInfoForm o2) {
       int result = 0;
        if(sortASC){
            result =   o1.getIsRead().compareTo(o2.getIsRead());
        }else{
            result = - o1.getIsRead().compareTo(o2.getIsRead());
        }
        return result;
    }


}
