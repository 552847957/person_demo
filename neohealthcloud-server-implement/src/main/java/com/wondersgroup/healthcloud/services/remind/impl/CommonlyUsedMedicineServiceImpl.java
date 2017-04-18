package com.wondersgroup.healthcloud.services.remind.impl;

import java.util.List;
import java.util.UUID;

import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.medicine.CommonlyUsedMedicine;
import com.wondersgroup.healthcloud.jpa.repository.remind.CommonlyUsedMedicineRepository;
import com.wondersgroup.healthcloud.services.remind.CommonlyUsedMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * Created by zhaozhenxing on 2017/04/15.
 */

@Service
public class CommonlyUsedMedicineServiceImpl implements CommonlyUsedMedicineService {
    @Autowired
    private CommonlyUsedMedicineRepository commonlyUsedMedicineRepository;

    @Override
    public List<CommonlyUsedMedicine> list(CommonlyUsedMedicine commonlyUsedMedicine) {
        List<CommonlyUsedMedicine> rtnList = null;
        try {
            rtnList = commonlyUsedMedicineRepository.findAll(Example.of(commonlyUsedMedicine));
        } catch (Exception ex) {
            //todo log
        }
        return rtnList;
    }

    @Override
    public List<CommonlyUsedMedicine> listTop5(String userId) {
        List<CommonlyUsedMedicine> rtnList = null;
        try {
            rtnList = commonlyUsedMedicineRepository.findTop5(userId);
        } catch (Exception ex) {
            //todo log
        }
        return rtnList;
    }

    @Override
    public CommonlyUsedMedicine detail(String id) {
        CommonlyUsedMedicine rtnEntity = null;
        try {
            rtnEntity = commonlyUsedMedicineRepository.findOne(id);
        } catch (Exception ex) {
            //todo log
        }
        return rtnEntity;
    }

    @Override
    public CommonlyUsedMedicine saveAndUpdate(CommonlyUsedMedicine commonlyUsedMedicine) {
        try {
            // todo determine insert or update
            /*if(commonlyUsedMedicine.getId() == null) { // insert
                commonlyUsedMedicine.setId(UUID.randomUUID().toString().replace("-", ""));
                commonlyUsedMedicine.setCreateTime(new Date());
            }
            commonlyUsedMedicine.setUpdateTime(new Date());*/
            return commonlyUsedMedicineRepository.save(commonlyUsedMedicine);
        } catch (Exception ex) {
            //todo log
        }
        return null;
    }

    @Override
    public int delete(String id) {
        try {
            commonlyUsedMedicineRepository.delete(id);
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }
}