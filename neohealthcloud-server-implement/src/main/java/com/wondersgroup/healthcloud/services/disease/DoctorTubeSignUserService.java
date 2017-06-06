package com.wondersgroup.healthcloud.services.disease;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;

import java.util.List;

/**
 * Created by limenghua on 2017/6/6.
 *
 * @author limenghua
 */
public interface DoctorTubeSignUserService {

    List<DoctorTubeSignUser> search(final DoctorTubeSignUser user, int page);
}
