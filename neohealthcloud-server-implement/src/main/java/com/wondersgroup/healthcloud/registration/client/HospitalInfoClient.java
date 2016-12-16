
package com.wondersgroup.healthcloud.registration.client;

import com.wondersgroup.healthcloud.registration.entity.response.HosInfoResponse;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.wsdl.GetHospitalInfoService;
import com.wondersgroup.healthcloud.wsdl.GetHospitalInfoServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class HospitalInfoClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(HospitalInfoClient.class);

    public HosInfoResponse getHospitalInfoList(String xml) {

        GetHospitalInfoService request = new GetHospitalInfoService();

        JAXBElement<String> xmlElements = new JAXBElement(
                new QName("http://impl.webservice.booking.icarefx.net","xmlString"), String.class, xml);
        request.setXmlString(xmlElements);

        GetHospitalInfoServiceResponse response = (GetHospitalInfoServiceResponse)getWebServiceTemplate()
                .marshalSendAndReceive(request);
        String xmlString = response.getReturn().getValue();
        HosInfoResponse customer = JaxbUtil.converyToJavaBean(xmlString, HosInfoResponse.class);
        return customer;
    }

}
