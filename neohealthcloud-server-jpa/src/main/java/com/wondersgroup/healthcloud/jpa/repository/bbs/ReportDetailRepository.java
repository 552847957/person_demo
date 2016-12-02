package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.ReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * Created by ys on 2016/09/13
 * 举报详情
 */
public interface ReportDetailRepository extends JpaRepository<ReportDetail, Integer> {

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_report_detail a where a.uid=?1 and a.report_id=?2 limit 1")
    ReportDetail findUserReport(String uid, Integer reportId);
}
