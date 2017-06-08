package com.wondersgroup.healthcloud.services.question.dto;

import com.wondersgroup.healthcloud.services.home.dto.familyHealth.FamilyMemberItemDTO;

import java.util.Comparator;

/**
 * Created by xianglinhai on 2016/12/23.
 */
public class DialogsComparable implements Comparator<Dialogs> {

    // 对象的排序方式[true 升、false 降]
    public static boolean sortASC = true;

    @Override
    public int compare(Dialogs o1, Dialogs o2) {
       int result = 0;
        if(sortASC){
            result =   o1.getIsCurrentDoctor().compareTo(o2.getIsCurrentDoctor());
        }else{
            result = - o1.getIsCurrentDoctor().compareTo(o2.getIsCurrentDoctor());
        }
        return result;
    }


}
