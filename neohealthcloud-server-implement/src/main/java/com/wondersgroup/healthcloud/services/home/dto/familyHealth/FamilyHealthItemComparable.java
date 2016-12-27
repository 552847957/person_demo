package com.wondersgroup.healthcloud.services.home.dto.familyHealth;

import java.util.Comparator;

/**
 * Created by xianglinhai on 2016/12/23.
 */
public class FamilyHealthItemComparable implements Comparator<FamilyMemberItemDTO> {

    // 对象的排序方式[true 升、false 降]
    public static boolean sortASC = true;

    @Override
    public int compare(FamilyMemberItemDTO o1, FamilyMemberItemDTO o2) {
       int result = 0;
        if(sortASC){
            result =   o1.getTestTime().compareTo(o2.getTestTime());
        }else{
            result = - o1.getTestTime().compareTo(o2.getTestTime());
        }
        return result;
    }
}
