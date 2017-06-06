package com.wondersgroup.healthcloud.services.disease;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentInfoDto;

import java.util.List;

/**
 * Created by limenghua on 2017/6/6.
 *
 * @author limenghua
 */
public interface DoctorTubeSignUserService {

    List<DoctorTubeSignUser> search(final DoctorTubeSignUser user, int page);

    /**
     * @param groupId  分组id
     * @param page     页码  不传默认第一页
     * @param pageSize 每页多少条数据,默认100条
     * @return
     */
    List<ResidentInfoDto> queryByGroup(String groupId, int page, int pageSize);
}
