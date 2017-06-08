package com.wondersgroup.healthcloud.services.question.dto;

import java.util.Comparator;

/**
 * Created by xianglinhai on 2016/12/23.
 */
public class DialogDoctorDateComparable implements Comparator<Dialogs> {

    // 对象的排序方式[true 升、false 降]
    public static boolean sortASC = true;

    @Override
    public int compare(Dialogs o1, Dialogs o2) {
        if(null == o1.lastDoctorDate || null == o2.lastDoctorDate){
            return 0;//错误
        }

       int result = 0;
        if(sortASC){
            result =   o1.lastDoctorDate.compareTo(o2.lastDoctorDate);
        }else{
            result = - o1.lastDoctorDate.compareTo(o2.lastDoctorDate);
        }
        return result;
    }


}
