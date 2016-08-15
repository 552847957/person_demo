package com.wondersgroup.healthcloud.services.user.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.wondersgroup.healthcloud.jpa.entity.activiti.HealthActivityDetail;
import com.wondersgroup.healthcloud.jpa.entity.activiti.HealthActivityInfo;
import com.wondersgroup.healthcloud.jpa.repository.activiti.HealthActivityDetailRepository;
import com.wondersgroup.healthcloud.jpa.repository.activiti.HealthActivityInfoRepository;
import com.wondersgroup.healthcloud.services.user.HealthActivityInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service()
public class HealthActivityInfoServiceImpl implements HealthActivityInfoService {
    @Autowired
    HealthActivityInfoRepository   activityRepo;

    @Autowired
    HealthActivityDetailRepository activityDetailRepo;

    @Autowired
    private DataSource             dataSource;

    private JdbcTemplate           jt;

    @Override
    public List<HealthActivityInfo> getHealthActivityInfos(String area, String type) {
        if (type.equals("0")) {
            //如果type=0只筛选区域，否则筛选条件为区域和类型
            return activityRepo.findActivitiesByArea(area);
        } else {
            return activityRepo.findActivitiesByAreaAndType(area, type);
        }
    }

    @Override
    public HealthActivityInfo getHealthActivityInfo(String activityid) {
        return activityRepo.findOne(activityid);
    }

    @Override
    public HealthActivityDetail findActivityDetailByAidAndRid(String activityId, String registerId) {
        return activityDetailRepo.findActivityDetailByAidAndRid(activityId, registerId);
    }

    @Override
    public List<HealthActivityInfo> getHealthActivityInfos(String area, String type, int pageNo, int pageSize) {

        String sql = "select *,case when (endtime < now()) THEN 1 else 0 end as overdue "
                + " from app_tb_healthactivity_info where" + " (province = '" + area + "' or city = '" + area
                + "' or county = '" + area + "')" + " and online_status =1 and del_flag = '0'";
        if (type.equals("0")) {
            sql += " and type <>0";
        } else {
            sql += " and type = '" + type + "'";
        }
        sql += " ORDER BY overdue asc ,starttime desc  limit " + (pageNo - 1) * pageSize + "," + (pageSize);
        List<Map<String, Object>> resourceList = getJt().queryForList(sql);
        List<HealthActivityInfo> list = Lists.newArrayList();
        for (Map<String, Object> map : resourceList) {
            list.add(new Gson().fromJson(new Gson().toJson(map), HealthActivityInfo.class));
        }
        return list;
    }

    /**
     * 获取jdbc template
     * @return
     */
    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }

}
