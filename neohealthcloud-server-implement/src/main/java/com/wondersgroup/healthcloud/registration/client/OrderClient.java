package com.wondersgroup.healthcloud.registration.client;

import com.wondersgroup.healthcloud.registration.entity.response.*;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.wsdl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Created by longshasha on 16/5/22.
 */
public class OrderClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(OrderClient.class);

    /**
     * 预约
     * @param xml
     * @return
     */
    public OrderResultResponse submitOrderByUserInfo(String xml) {

        SubmitOrderByUserInfoService request = new SubmitOrderByUserInfoService();
        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlString"), String.class, xml);
        request.setXmlString(xmlElements);

        SubmitOrderByUserInfoServiceResponse response = (SubmitOrderByUserInfoServiceResponse)getWebServiceTemplate()
                .marshalSendAndReceive(
                        request);
        String xmlString = response.getReturn().getValue();
        OrderResultResponse customer = JaxbUtil.converyToJavaBean(xmlString, OrderResultResponse.class);
        return customer;
    }

    /**
     * 查询可预约号源信息(排班)
     * @param xml
     * @return
     */
    public NumSourceInfoResponse getOrderNumInfoList(String xml) {

        GetOrderNumInfoService request = new GetOrderNumInfoService();
        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlString"), String.class, xml);
        request.setXmlString(xmlElements);

        GetOrderNumInfoServiceResponse response;
        NumSourceInfoResponse  customer = new NumSourceInfoResponse();
        try {
            response = (GetOrderNumInfoServiceResponse)getWebServiceTemplate()
                    .marshalSendAndReceive(
                            request);
            String xmlString = response.getReturn().getValue();
            customer = JaxbUtil.converyToJavaBean(xmlString, NumSourceInfoResponse.class);
        }catch (Exception e){
            log.error("OrderClient-----getOrderNumInfoList:"+e.getLocalizedMessage());
        }

        return customer;
    }

    /**
     * 查询可预约时间段(根据排班Id查询)
     * @param xml
     * @return
     */
    public SegmentNumberInfoResponse getOrderSegmentNumberInfoList(String xml) {

        QueryTimeSegmentListService request = new QueryTimeSegmentListService();
        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlStr"), String.class, xml);
        request.setXmlStr(xmlElements);
        QueryTimeSegmentListServiceResponse response;
        SegmentNumberInfoResponse  customer = new SegmentNumberInfoResponse();
        try {
            response = (QueryTimeSegmentListServiceResponse)getWebServiceTemplate()
                    .marshalSendAndReceive(
                            request);
            String xmlString = response.getReturn().getValue();
            customer = JaxbUtil.converyToJavaBean(xmlString, SegmentNumberInfoResponse.class);
        }catch (Exception e){
            log.error("OrderClient-----getOrderSegmentNumberInfoList:"+e.getLocalizedMessage());
        }

        return customer;
    }


    /**
     * 取消预约
     * @param xml
     * @return
     */
    public OrderCancelResponse orderCancel(String xml) {

        OrderCancelInfoService request = new OrderCancelInfoService();
        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlString"), String.class, xml);
        request.setXmlString(xmlElements);

        OrderCancelInfoServiceResponse response = (OrderCancelInfoServiceResponse)getWebServiceTemplate()
                .marshalSendAndReceive(
                        request);
        String xmlString = response.getReturn().getValue();
        OrderCancelResponse customer = JaxbUtil.converyToJavaBean(xmlString, OrderCancelResponse.class);
        return customer;
    }


    /**
     * 查询预约单列表
     * @param xml
     * @return
     */
    public OrderInfoListResponse getOrderInfoList(String xml) {

        QueryOrderInfoListService request = new QueryOrderInfoListService();
        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlStr"), String.class, xml);
        request.setXmlStr(xmlElements);

        QueryOrderInfoListServiceResponse response = (QueryOrderInfoListServiceResponse)getWebServiceTemplate()
                .marshalSendAndReceive(
                        request);
        String xmlString = response.getReturn().getValue();
        OrderInfoListResponse customer = JaxbUtil.converyToJavaBean(xmlString, OrderInfoListResponse.class);
        return customer;
    }

    /**
     * 查询预约单详情
     * @param xml
     * @return
     */
    public OrderDetailResponse getOrderDetail(String xml) {

        GetOrderDetailInfoService request = new GetOrderDetailInfoService();
        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlString"), String.class, xml);
        request.setXmlString(xmlElements);

        GetOrderDetailInfoServiceResponse response = (GetOrderDetailInfoServiceResponse)getWebServiceTemplate()
                .marshalSendAndReceive(
                        request);
        String xmlString = response.getReturn().getValue();
        OrderDetailResponse customer = JaxbUtil.converyToJavaBean(xmlString, OrderDetailResponse.class);
        return customer;
    }



}
