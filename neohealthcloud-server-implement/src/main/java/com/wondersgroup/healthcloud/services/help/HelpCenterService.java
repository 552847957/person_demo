package com.wondersgroup.healthcloud.services.help;

import com.wondersgroup.healthcloud.jpa.entity.help.HelpCenter;

import java.util.List;

/**
 * Created by shenbin on 16/8/12.
 */
public interface HelpCenterService {

    List<HelpCenter> findByIsVisable(String isVisable);
}
