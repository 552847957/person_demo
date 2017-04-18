package com.wondersgroup.healthcloud.services.medicine;

import com.wondersgroup.healthcloud.jpa.entity.medicine.Medicine;

import java.util.List;

/**
 * Created by zhaozhenxing on 2017/04/15.
 */

public interface MedicineService {
    List<Medicine> list(String type);
    Medicine detail(String id);
    Medicine saveAndUpdate(Medicine medicine);
}