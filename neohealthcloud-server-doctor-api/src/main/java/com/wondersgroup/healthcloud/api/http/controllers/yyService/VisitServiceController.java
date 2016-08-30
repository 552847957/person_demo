package com.wondersgroup.healthcloud.api.http.controllers.yyService;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountNoneException;
import com.wondersgroup.healthcloud.services.yyService.VisitDoctorService;
import com.wondersgroup.healthcloud.services.yyService.dto.YYExecDemoInfo;
import com.wondersgroup.healthcloud.services.yyService.dto.YYVisitOrderInfo;
import com.wondersgroup.healthcloud.services.yyService.dto.YYVisitUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanshuai on 2016-08-30
 * 医养融合 上门服务
 */
@RestController
@RequestMapping("/api/medicineSupport/visitService")
public class VisitServiceController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private VisitDoctorService visitDoctorService;

    private static Integer ORDER_PAGE_SIZE = 10;

    /**
     * 服务签到
     */
    @ResponseBody
    @RequestMapping(value = "/checkIn", method = RequestMethod.GET)
    public JsonResponseEntity<String> checkIn(@RequestParam String doctorId, @RequestParam String workOrderNo) {

        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        String personcard = this.checkAndGetPersoncard(doctorId);
        Boolean isCheckInOk = this.visitDoctorService.checkInVisitService(personcard, workOrderNo);

        if (isCheckInOk){
            response.setMsg("签到成功");
        }else {
            response.setCode(2040);
            response.setMsg("签到失败");
        }
        return  response;
    }

    /**
     * 服务订单列表
     */
    @ResponseBody
    @RequestMapping(value = "/orderList", method = RequestMethod.GET)
    public JsonListResponseEntity<YYVisitOrderInfo> orderList(@RequestParam String doctorId,
                                                              @RequestParam(defaultValue="1") Integer type,
                                                              @RequestParam(required = false, defaultValue="1") Integer flag) {

        JsonListResponseEntity<YYVisitOrderInfo> response = new JsonListResponseEntity<>();
        String personcard = this.checkAndGetPersoncard(doctorId);
        Boolean hasMore = false;
        List<YYVisitOrderInfo> orderList = this.visitDoctorService.getOrderList(personcard, type, flag, ORDER_PAGE_SIZE);
        if (orderList != null && orderList.size()==ORDER_PAGE_SIZE){
            List<YYVisitOrderInfo> moreList = this.visitDoctorService.getOrderList(personcard, type, flag+1, ORDER_PAGE_SIZE);
            if (moreList != null && !moreList.isEmpty()){
                hasMore = true;
            }
        }
        response.setContent(orderList, hasMore, null, String.valueOf(flag+1));
        return  response;
    }

    /**
     * 获取要服务的老人信息
     * elderid
     */
    @ResponseBody
    @RequestMapping(value = "/getElderInfo", method = RequestMethod.GET)
    public JsonResponseEntity<YYVisitUserInfo> getElderInfo(@RequestParam String doctorId, @RequestParam String elderid) {

        JsonResponseEntity<YYVisitUserInfo> response = new JsonResponseEntity<>();
        String personcard = this.checkAndGetPersoncard(doctorId);
        YYVisitUserInfo userInfo = this.visitDoctorService.getElderInfo(personcard, elderid);

        response.setData(userInfo);
        return  response;
    }

    /**
     * 获取表单录入信息
     */
    @ResponseBody
    @RequestMapping(value = "/execDemo", method = RequestMethod.GET)
    public JsonListResponseEntity<YYExecDemoInfo> execDemo(@RequestParam String doctorId, @RequestParam String fwnrid,
                                                           @RequestParam String workOrderNo){
        JsonListResponseEntity<YYExecDemoInfo> response = new JsonListResponseEntity<>();
        String personcard = this.checkAndGetPersoncard(doctorId);
        List<YYExecDemoInfo> list = this.visitDoctorService.execDemo(personcard, fwnrid, workOrderNo);
        response.setContent(list, false, null, null);
        return response;
    }

    public static void main(String[] args){
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("xh", "1");
        data.put("execresult", "1");
        data.put("execmemo", new String[]{"aa","bb"});
        dataList.add(data);
    }

    /**
     *  提交表单录入信息
     */
    @ResponseBody
    @RequestMapping(value = "/postExecDemo", method = RequestMethod.POST)
    public JsonResponseEntity<Boolean> postExecDemo(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        String doctorId = reader.readString("doctorId", false);
        String workOrderNo = reader.readString("workOrderNo", false);
        String data = reader.readString("data", false);

        JsonResponseEntity<Boolean> response = new JsonResponseEntity<>();
        String personcard = this.checkAndGetPersoncard(doctorId);
        Boolean isOk = this.visitDoctorService.submitExecDemo(personcard, workOrderNo, data);
        response.setMsg(isOk ? "提交成功" : "提交失败");
        //response.setData(isOk);
        return response;
    }

    private String checkAndGetPersoncard(String doctorId){
        Map<String, Object> doctorInfo = doctorService.findDoctorInfoByUid(doctorId);
        if (null == doctorInfo){
            throw new ErrorDoctorAccountNoneException();
        }
        String personcard = null == doctorInfo.get("idcard") ? "" : doctorInfo.get("idcard").toString();
        return personcard;
    }
}
