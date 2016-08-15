package com.wondersgroup.healthcloud.services.help.impl;

import com.wondersgroup.healthcloud.jpa.entity.help.HelpCenter;
import com.wondersgroup.healthcloud.jpa.repository.help.HelpCenterRepository;
import com.wondersgroup.healthcloud.services.help.HelpCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shenbin on 16/8/12.
 */
@Service
public class HelpCenterServiceImpl implements HelpCenterService{

    @Autowired
    private HelpCenterRepository helpCenterRepository;

    @Override
    public List<HelpCenter> findByIsVisable(String isVisable) {
        return helpCenterRepository.findByIsVisable(isVisable);
    }
}
