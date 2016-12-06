package com.wondersgroup.healthcloud.registration.client;

import com.wondersgroup.healthcloud.registration.entity.response.DoctInfoResponse;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.wsdl.GetDoctInfoService;
import com.wondersgroup.healthcloud.wsdl.GetDoctInfoServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Created by longshasha on 16/5/22.
 */
public class DoctInfoClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(DoctInfoClient.class);

    public DoctInfoResponse getDoctInfoList(String xml) {

        GetDoctInfoService request = new GetDoctInfoService();

        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlString"), String.class, xml);
        request.setXmlString(xmlElements);
        GetDoctInfoServiceResponse response= (GetDoctInfoServiceResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        String xmlString = response.getReturn().getValue();
        DoctInfoResponse customer = JaxbUtil.converyToJavaBean(xmlString, DoctInfoResponse.class);
        return customer;
    }
}
