package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorTemplateRepository extends JpaRepository<DoctorTemplate, String> {

    @Query("select dt from DoctorTemplate dt where dt.doctorId=?1 and dt.type=?2 order by dt.updateTime desc")
    List<DoctorTemplate> findByDoctorIdAndType(String doctorId, String type);
}
