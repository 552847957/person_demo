package com.wondersgroup.healthcloud.registration.client;

import com.wondersgroup.healthcloud.registration.entity.response.TopDeptInfoResponse;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.wsdl.GetDeptInfoTopService;
import com.wondersgroup.healthcloud.wsdl.GetDeptInfoTopServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Created by longshasha on 16/5/22.
 */

/**
 * 获取一级科室列表
 */
public class TopDeptInfoTopClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(TopDeptInfoTopClient.class);

    public TopDeptInfoResponse GetTopDeptInfo(String xml) {

        GetDeptInfoTopService request = new GetDeptInfoTopService();
        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlString"), String.class, xml);
        request.setXmlString(xmlElements);

        GetDeptInfoTopServiceResponse response = (GetDeptInfoTopServiceResponse)getWebServiceTemplate()
                .marshalSendAndReceive(
                        request);
        String xmlString = response.getReturn().getValue();
        TopDeptInfoResponse customer = JaxbUtil.converyToJavaBean(xmlString, TopDeptInfoResponse.class);
        return customer;
    }
}
