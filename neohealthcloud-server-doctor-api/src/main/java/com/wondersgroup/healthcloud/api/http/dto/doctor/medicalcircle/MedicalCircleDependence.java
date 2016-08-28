//package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;
//
//import com.wonders.healthcloud.modules.user.facade.services.DoctorInfoService;
//import com.wondersgroup.hs.healthcloud.helper.DictCache;
//import com.wondersgroup.hs.healthcloud.helper.DictUtils;
//import com.wondersgroup.hs.healthcloud.helper.DoctorUtils;
//import com.wondersgroup.hs.healthcloud.service.MedicalCircleService;
//
///**
// * Created by Yoda on 2015/9/15.
// */
//public class MedicalCircleDependence {
//
//    private MedicalCircleService mcService;
//    private DictCache dictCache;
//    private DoctorUtils docUtils;
//
//    public MedicalCircleDependence(MedicalCircleService mcService,DoctorUtils docUtils,DictCache dictCache) {
//        this.mcService=mcService;
//        this.docUtils = docUtils;
//        this.dictCache = dictCache;
//    }
//
//    public MedicalCircleService getMcService() {
//        return mcService;
//    }
//
//    public DictCache getDictCache() {
//        return dictCache;
//    }
//
//    public DoctorUtils getDocUtils() {
//        return docUtils;
//    }
//}
