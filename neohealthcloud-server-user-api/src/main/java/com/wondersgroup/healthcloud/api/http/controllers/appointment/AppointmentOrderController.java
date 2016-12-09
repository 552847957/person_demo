package com.wondersgroup.healthcloud.api.http.controllers.appointment;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.appointment.AppointmentOrderDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentContact;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.services.appointment.AppointmentContactService;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;
import com.wondersgroup.healthcloud.services.appointment.exception.ErrorAppointmentException;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by longshasha on 16/12/8.
 */
@RestController
@RequestMapping("/api/reservation")
public class AppointmentOrderController {

    @Autowired
    private AppointmentApiService appointmentApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AppointmentContactService appointmentContactService;

    /**
     * 发送(验证)短信
     *
     * @param uid 给注册用户发送验证码
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/sms", method = RequestMethod.GET)
    public JsonResponseEntity<String> sendSMS(@RequestParam("uid") String uid) {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        RegisterInfo registerInfo = userService.getOneNotNull(uid);
        if(StringUtils.isBlank(registerInfo.getRegmobilephone())){
            throw new ErrorAppointmentException();
        }
        // todo  编辑预约挂号短信文案
        userAccountService.getVerifyCode(registerInfo.getRegmobilephone(), 6);
        response.setData("发送成功");
        return response;
    }


    /**
     * 预约
     *
     * @param request
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public JsonResponseEntity<AppointmentOrderDTO>  getUserReservation(@RequestBody String request) {
        JsonResponseEntity<AppointmentOrderDTO> body = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(request);
        String scheduleId = reader.readString("schedule_id", false);
        String contactId = reader.readString("contact_id", false);
        String code = reader.readString("code", false);//手机验证码
        String orderType = reader.readString("type", false);//预约类型1:医生 2:科室

        AppointmentContact contact = appointmentContactService.getAppointmentContactById(contactId);
        RegisterInfo registerInfo = userService.getOneNotNull(contact.getUid());


        Boolean success = userAccountService.validateCode(registerInfo.getRegmobilephone(), code,true);
        if(!success){
            body.setCode(1002);
            body.setMsg("短信验证码验证错误");
            return body;
        }

        OrderDto appointmentOrder = appointmentApiService.submitUserReservation(contactId, scheduleId, orderType);
        AppointmentOrderDTO orderDTO = new AppointmentOrderDTO(appointmentOrder);

        body.setData(orderDTO);
        return body;
    }

    /**
     * 取消预约
     * @param id
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.DELETE)
    public JsonResponseEntity<AppointmentOrderDTO> cancel(@RequestParam String id) {
        JsonResponseEntity<AppointmentOrderDTO> body = new JsonResponseEntity<>();
        appointmentApiService.cancelReservationOrderById(id);
        OrderDto orderDto = appointmentApiService.findOrderByUidOrId(id,null,null,false).get(0);
        AppointmentOrderDTO entity = new AppointmentOrderDTO(orderDto);
        body.setData(entity);
        body.setMsg("取消成功");
        return body;
    }


    /**
     * 我的预约列表
     *
     * @param uid
     * @param flag
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/orderHistory", method = RequestMethod.GET)
    public JsonListResponseEntity<AppointmentOrderDTO> history(@RequestParam(required = true) String uid,
                                                               @RequestParam(value = "flag", defaultValue = "1", required = false) String flag) {
        JsonListResponseEntity<AppointmentOrderDTO> response = new JsonListResponseEntity<>();
        int pageSize = 10;
        boolean has_more = false;
        List<OrderDto> appointmentOrders = appointmentApiService.findOrderByUidOrId(uid, Integer.valueOf(flag), pageSize, true);

        LinkedList<AppointmentOrderDTO> result = Lists.newLinkedList();
        for (OrderDto order : appointmentOrders) {
            if(result.size()>pageSize)
                break;
            result.add(new AppointmentOrderDTO(order));
        }

        response.setContent(result, has_more, null, has_more?String.valueOf(Integer.valueOf(flag) + 1):flag);
        return response;
    }

    /**
     * 预约详情
     * @param id
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/orderDetail", method = RequestMethod.GET)
    public JsonResponseEntity<AppointmentOrderDTO> orderDetail(@RequestParam(required = true) String id) {
        JsonResponseEntity<AppointmentOrderDTO> response = new JsonResponseEntity<>();
        OrderDto orderDto = appointmentApiService.findOrderByUidOrId(id,null,null,false).get(0);

        if (orderDto != null) {
            AppointmentOrderDTO appointmentOrderDTO = new AppointmentOrderDTO(orderDto);
            response.setData(appointmentOrderDTO);
        }

        return response;
    }

}
