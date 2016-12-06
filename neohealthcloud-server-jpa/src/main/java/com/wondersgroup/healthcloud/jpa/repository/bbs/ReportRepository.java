package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * Created by ys on 2016/09/13
 * 举报
 */
public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query(nativeQuery = true,value="select * from tb_bbs_report a where a.target_id=?1 and a.target_type=?2 limit 1")
    Report findReportInfo(Integer targetId, Integer targetType);
}
