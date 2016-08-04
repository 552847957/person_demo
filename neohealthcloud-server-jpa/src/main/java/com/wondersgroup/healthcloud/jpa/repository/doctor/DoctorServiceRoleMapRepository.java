package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceRoleMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Created by longshasha on 16/8/2.
 */
public interface DoctorServiceRoleMapRepository extends JpaRepository<DoctorServiceRoleMap,String> {

    @Query(" select a from DoctorServiceRoleMap a where a.delFlag ='0' and a.roleId in ?1 GROUP BY a.serviceId")
    List<DoctorServiceRoleMap> findServicesByRoles(Collection<String> roleList);

}
