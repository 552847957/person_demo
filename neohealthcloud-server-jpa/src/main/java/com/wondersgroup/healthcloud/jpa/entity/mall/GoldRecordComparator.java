package com.wondersgroup.healthcloud.jpa.entity.mall;

import java.util.Comparator;

public class GoldRecordComparator implements Comparator<GoldRecord>{

    @Override
    public int compare(GoldRecord o1, GoldRecord o2) {
        return (o1.getRestNum()<o2.getRestNum()?-1:(o1.getRestNum()<o2.getRestNum()?0:1));
    }

}
