package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.ReportFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * Created by zhuchunliu on 2017/4/18.
 */
public interface ReportFollowRepository extends JpaRepository<ReportFollow, String> {

    @Query(nativeQuery = true,value = "select * from app_tb_report_follow a where a.del_flag = '0' and a.registerid=?1 " +
            " order by follow_date desc limit 1")
    ReportFollow getReport(String registerid);


}
