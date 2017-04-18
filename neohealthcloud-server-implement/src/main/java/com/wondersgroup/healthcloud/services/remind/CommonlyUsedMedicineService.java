package com.wondersgroup.healthcloud.services.remind;

import com.wondersgroup.healthcloud.jpa.entity.medicine.CommonlyUsedMedicine;

import java.util.List;

/**
 * Created by zhaozhenxing on 2017/04/15.
 */

public interface CommonlyUsedMedicineService {
    List<CommonlyUsedMedicine> list(CommonlyUsedMedicine commonlyUsedMedicine);
    List<CommonlyUsedMedicine> listTop5(String userId);
    CommonlyUsedMedicine detail(String id);
    CommonlyUsedMedicine saveAndUpdate(CommonlyUsedMedicine commonlyUsedMedicine);
    int delete(String id);
}