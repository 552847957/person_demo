package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceRoleMap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Created by longshasha on 16/8/2.
 */
public interface DoctorServiceRoleMapRepository extends JpaRepository<DoctorServiceRoleMap, String> {

    @Query(" select a from DoctorServiceRoleMap a where a.delFlag ='0' and a.roleId in ?1 GROUP BY a.serviceId")
    List<DoctorServiceRoleMap> findServicesByRoles(Collection<String> roleList);

    List<DoctorServiceRoleMap> findAllByNameLike(String key, Pageable pageable);

    @Modifying
    @Query("update DoctorServiceRoleMap a set a.delFlag =1 where a.id = :id")
    int deleteDoctorServiceRoleMap(@Param("id") long id);
}
