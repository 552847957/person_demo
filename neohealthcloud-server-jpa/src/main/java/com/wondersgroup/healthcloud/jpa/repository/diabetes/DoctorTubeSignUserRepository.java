package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by longshasha on 17/5/16.
 */
public interface DoctorTubeSignUserRepository extends JpaRepository<DoctorTubeSignUser, String>, JpaSpecificationExecutor<DoctorTubeSignUser> {
}
