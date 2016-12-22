package com.wondersgroup.healthcloud.api.http.controllers.appointment;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentOrder;
import com.wondersgroup.healthcloud.jpa.repository.appointment.OrderRepository;
import com.wondersgroup.healthcloud.registration.client.OrderClient;
import com.wondersgroup.healthcloud.registration.entity.request.OrderListR;
import com.wondersgroup.healthcloud.registration.entity.request.OrderListRequest;
import com.wondersgroup.healthcloud.registration.entity.request.RequestMessageHeader;
import com.wondersgroup.healthcloud.registration.entity.response.OrderInfo;
import com.wondersgroup.healthcloud.registration.entity.response.OrderInfoListResponse;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.services.appointment.AppointmentService;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.utils.registration.SignatureGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by longshasha on 16/12/15.
 * 更新最近两天的订单状态
 */

@RestController
@RequestMapping(value = "/api/reservation/job")
public class AppointmentOrderJobController {

    private static final Logger log = Logger.getLogger(AppointmentOrderJobController.class);
    private ExecutorService executor = Executors.newFixedThreadPool(30);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private Environment environment;

    @Autowired
    private AppointmentApiService appointmentApiService;




    @RequestMapping(value = "/closeNumberSource", method = RequestMethod.GET)
    public JsonResponseEntity closeNumberSource(@RequestParam(required = true,value = "order_id") String orderId) {
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        appointmentApiService.closeNumberSourceByOrderId(orderId);
        return  responseEntity;
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public JsonResponseEntity updateOrderStatus() {
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        log.info("------------------AppointmentOrderJob  start  -------------------");

        List<OrderDto> orderList = appointmentService.findOrderListNeedUpdateStatus();

        List<FutureTask<Integer[]>> futureTasks = Lists.newArrayList();
        if(orderList.size()>0){
            for(OrderDto orderDto : orderList){
                String cardType = "01";
                String cardId = orderDto.getUserCardId();
                if(StringUtils.isNotBlank(cardId) && StringUtils.isNotBlank(cardType)){
                    AppointmentOrderTask appointmentOrderTask = new AppointmentOrderTask(cardType,cardId);
                    FutureTask<Integer[]> task1 = new FutureTask<Integer[]>(appointmentOrderTask);
                    executor.submit(task1);
                    futureTasks.add(task1);
                }
            }
            for (int i = 0;i<futureTasks.size();i++){
                getResult(futureTasks.get(i));
            }

        }

        log.info("------------------AppointmentOrderJob  end  -------------------");
        responseEntity.setData("success");
        return responseEntity;
    }


    public class AppointmentOrderTask implements Callable {
        private String cardId;
        private String cardType;

        @Override
        public Object call() throws Exception {
            List<OrderInfo> orderInfoList = getOrderList(cardType,cardId);
            if(orderInfoList.size()>0){
                for (OrderInfo orderInfo : orderInfoList){
                    String orderId = orderInfo.getOrderId();
                    if(StringUtils.isNotBlank(orderId)){
                        AppointmentOrder order = orderRepository.findOrderByOrderId(orderId);
                        //修改订单状态
                        if(order!=null && StringUtils.isNotBlank(order.getId())){
                            order.setOrderStatus(orderInfo.getOrderStatus());
                            orderRepository.saveAndFlush(order);
                        }

                    }
                }
            }

            return null;
        }

        public AppointmentOrderTask(String cardType,String cardId) {
            super();
            this.cardId = cardId;
            this.cardType = cardType;
        }
    }

    private List<OrderInfo> getOrderList(String cardType,String cardId) {

        OrderListRequest orderListRequest = new OrderListRequest();
        orderListRequest.requestMessageHeader = new RequestMessageHeader(environment);
        OrderListR orderListR = new OrderListR();
        orderListR.setUserCardType(cardType);
        orderListR.setUserCardId(cardId);

        orderListR.setVisitStartTime(DateFormatter.dateFormat(DateUtils.addDay(new Date(), -1)));
        orderListR.setVisitEndTime(DateFormatter.dateFormat(new Date()));
        orderListRequest.orderListR = orderListR;

        orderListRequest.requestMessageHeader.setSign(SignatureGenerator.generateSignature(orderListRequest));
        String xmlRequest = JaxbUtil.convertToXml(orderListRequest);

        OrderInfoListResponse orderInfoListResponse = orderClient.getOrderInfoList(xmlRequest);
        List<OrderInfo> orderInfos = Lists.newArrayList();
        if("0".equals(orderInfoListResponse.messageHeader.getCode())){
            orderInfos = orderInfoListResponse.orderInfoList;
        }else{
            log.error("getOrderInfoList:"+orderInfoListResponse.messageHeader.getDesc());
        }

        return orderInfos;
    }

    private <T> T getResult(FutureTask<T> task) {
        while (true) {
            if (task.isDone() && !task.isCancelled()) {
                break;
            }
        }
        try {
            return task.get();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
}

