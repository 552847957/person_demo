package com.wondersgroup.healthcloud.api.http.dto.medicalcircle;

import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.services.medicalcircle.MedicalCircleService;

/**
 * Created by Yoda on 2015/9/15.
 */
public class MedicalCircleDependence {

    private MedicalCircleService mcService;
    private DictCache dictCache;

    public MedicalCircleDependence(MedicalCircleService mcService, DictCache dictCache) {
        this.mcService=mcService;
        this.dictCache = dictCache;
    }

    public MedicalCircleService getMcService() {
        return mcService;
    }

    public DictCache getDictCache() {
        return dictCache;
    }

}
