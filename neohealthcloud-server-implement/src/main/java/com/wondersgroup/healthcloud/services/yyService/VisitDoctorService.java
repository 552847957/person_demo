package com.wondersgroup.healthcloud.services.yyService;


import com.wondersgroup.healthcloud.services.yyService.dto.YYDoctorInfo;
import com.wondersgroup.healthcloud.services.yyService.dto.YYExecDemoInfo;
import com.wondersgroup.healthcloud.services.yyService.dto.YYVisitOrderInfo;
import com.wondersgroup.healthcloud.services.yyService.dto.YYVisitUserInfo;

import java.util.List;

/**
 * Created by ys on 2016/5/18.
 *
 */
public interface VisitDoctorService {

    /**
     * 获取医养融合医生个人信息
     * @param persioncard
     * @return
     */
    public YYDoctorInfo getYYDoctorUserInfo(String persioncard, Boolean get_real_data);

    /**
     * 上门服务签到
     * @param personcard
     * @param workOrderNo
     * @return
     */
    public Boolean checkInVisitService(String personcard, String workOrderNo);


    /**
     * 订单列表
     */
    public List<YYVisitOrderInfo> getOrderList(String personcard, Integer type, Integer page, Integer pageSize);

    /**
     * 获取服务老人信息
     * @param personcard 当前查询医生的身份证
     * @param elderid 当前要查询的老人id
     * @return
     */
    public YYVisitUserInfo getElderInfo(String personcard, String elderid);

    /**
     * 需要录入的表单信息
     * @param fwnrid 服务内容id
     * @param  workOrderNo 订单id
     * @return
     */
    public List<YYExecDemoInfo> execDemo(String personcard, String fwnrid, String workOrderNo);

    /**
     * 提交待录入的表单信息
     * @return
     */
    public Boolean submitExecDemo(String personcard, String workOrderNo, String data);
}
