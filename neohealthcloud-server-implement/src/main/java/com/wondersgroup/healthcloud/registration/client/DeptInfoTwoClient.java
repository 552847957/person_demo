package com.wondersgroup.healthcloud.registration.client;

import com.wondersgroup.healthcloud.registration.entity.response.TwoDeptInfoResponse;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.wsdl.GetDeptInfoTwoService;
import com.wondersgroup.healthcloud.wsdl.GetDeptInfoTwoServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Created by longshasha on 16/5/22.
 */
public class DeptInfoTwoClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(DeptInfoTwoClient.class);

    public TwoDeptInfoResponse getDeptInfoTwoList(String xml) {

        GetDeptInfoTwoService request = new GetDeptInfoTwoService();

        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlString"), String.class, xml);
        request.setXmlString(xmlElements);
        GetDeptInfoTwoServiceResponse response= (GetDeptInfoTwoServiceResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        String xmlString = response.getReturn().getValue();
        TwoDeptInfoResponse customer = JaxbUtil.converyToJavaBean(xmlString, TwoDeptInfoResponse.class);
        return customer;
    }
}
