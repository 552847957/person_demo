package com.wondersgroup.healthcloud.jpa.repository.circle;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.circle.CircleReport;

public interface CircleReportRepository extends JpaRepository<CircleReport,Integer>{

    CircleReport findByReportid(String id);
}
