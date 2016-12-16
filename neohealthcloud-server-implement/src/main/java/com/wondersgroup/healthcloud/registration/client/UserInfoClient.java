package com.wondersgroup.healthcloud.registration.client;

import com.wondersgroup.healthcloud.registration.entity.response.MemberInfoResultResponse;
import com.wondersgroup.healthcloud.registration.entity.response.TwoDeptInfoResponse;
import com.wondersgroup.healthcloud.registration.entity.response.UserInfoResultResponse;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.wsdl.RegisterOrUpdateMemberInfoService;
import com.wondersgroup.healthcloud.wsdl.RegisterOrUpdateMemberInfoServiceResponse;
import com.wondersgroup.healthcloud.wsdl.RegisterOrUpdateUserInfoService;
import com.wondersgroup.healthcloud.wsdl.RegisterOrUpdateUserInfoServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Created by longshasha on 16/12/8.
 * 预约挂号 用户注册或添加成员
 */
public class UserInfoClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(DeptInfoTwoClient.class);

    /**
     * 预约挂号用户注册 (没有更新功能)
     * @param xml
     * @return
     */
    public UserInfoResultResponse registerOrUpdateUserInfo(String xml) {

        RegisterOrUpdateUserInfoService request = new RegisterOrUpdateUserInfoService();

        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlStr"), String.class, xml);
        request.setXmlStr(xmlElements);
        RegisterOrUpdateUserInfoServiceResponse response= (RegisterOrUpdateUserInfoServiceResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        String xmlString = response.getReturn().getValue();
        UserInfoResultResponse customer = JaxbUtil.converyToJavaBean(xmlString, UserInfoResultResponse.class);
        return customer;
    }


    /**
     * 第三方只提供注册没有更新功能
     * @param xml
     * @return
     */
    public MemberInfoResultResponse registerOrUpdateMemberInfo(String xml) {

        RegisterOrUpdateMemberInfoService request = new RegisterOrUpdateMemberInfoService();

        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlStr"), String.class, xml);
        request.setXmlStr(xmlElements);
        RegisterOrUpdateMemberInfoServiceResponse response= (RegisterOrUpdateMemberInfoServiceResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        String xmlString = response.getReturn().getValue();
        MemberInfoResultResponse customer = JaxbUtil.converyToJavaBean(xmlString, MemberInfoResultResponse.class);
        return customer;
    }
}
