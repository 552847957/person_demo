package com.wondersgroup.healthcloud.services.medicine.impl;

import java.util.List;

import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.medicine.Medicine;
import com.wondersgroup.healthcloud.jpa.repository.medicine.MedicineRepository;
import com.wondersgroup.healthcloud.services.medicine.MedicineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

/**
 * Created by zhaozhenxing on 2017/04/15.
 */

@Service
public class MedicineServiceImpl implements MedicineService {

    protected static final Logger logger = LoggerFactory.getLogger(MedicineServiceImpl.class);

    @Autowired
    private MedicineRepository medicineRepository;

    @Override
    public List<Medicine> list() {
        List<Medicine> rtnList = null;
        try {
            rtnList = medicineRepository.findAllByOrderByIdAsc();
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return rtnList;
    }

    @Override
    public Medicine detail(String id) {
        Medicine rtnEntity = null;
        try {
            rtnEntity = medicineRepository.findOne(id);
        } catch (Exception ex) {
            //todo log
        }
        return rtnEntity;
    }

    @Override
    public Medicine saveAndUpdate(Medicine medicine) {
        try {
            // todo determine insert or update
            /*if(medicine.getId() == null) { // insert
                medicine.setId(UUID.randomUUID().toString().replace("-", ""));
                medicine.setCreateTime(new Date());
            }
            medicine.setUpdateTime(new Date());*/
            return medicineRepository.save(medicine);
        } catch (Exception ex) {
            //todo log
        }
        return null;
    }
}