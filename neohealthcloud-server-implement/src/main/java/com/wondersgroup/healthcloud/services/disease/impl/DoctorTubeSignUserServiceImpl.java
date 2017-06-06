package com.wondersgroup.healthcloud.services.disease.impl;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.services.disease.DoctorTubeSignUserService;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentInfoDto;

import java.util.List;

/**
 * Created by limenghua on 2017/6/6.
 *
 * @author limenghua
 */
public class DoctorTubeSignUserServiceImpl implements DoctorTubeSignUserService {

    @Override
    public List<DoctorTubeSignUser> search(DoctorTubeSignUser user, int page) {

        return null;
    }

    @Override
    public List<ResidentInfoDto> queryByGroup(String groupId, int page, int pageSize) {
        return null;
    }
}
