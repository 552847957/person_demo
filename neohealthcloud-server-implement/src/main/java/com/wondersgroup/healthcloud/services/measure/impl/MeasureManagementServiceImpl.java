package com.wondersgroup.healthcloud.services.measure.impl;

import com.wondersgroup.healthcloud.jpa.entity.measure.MeasureManagement;
import com.wondersgroup.healthcloud.jpa.repository.measure.MeasureManagementRepository;
import com.wondersgroup.healthcloud.services.measure.MeasureManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Jeffrey on 16/8/21.
 */
@Service
public class MeasureManagementServiceImpl implements MeasureManagementService {

    @Autowired
    private MeasureManagementRepository managementRepository;

    @Override
    public List<MeasureManagement> displays() {
        return managementRepository.findByDisplayTrueOrderByPrioritiesDesc();
    }
}
