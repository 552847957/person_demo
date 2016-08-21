package com.wondersgroup.healthcloud.jpa.repository.measure;

import com.wondersgroup.healthcloud.jpa.entity.measure.MeasureManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Jeffrey on 16/8/21.
 */
public interface MeasureManagementRepository extends JpaRepository<MeasureManagement, Long> {

    List<MeasureManagement> findByDisplayTrueOrderByPrioritiesDesc();

}
