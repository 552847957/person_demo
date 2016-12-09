
package com.wondersgroup.healthcloud.configuration;

import com.wondersgroup.healthcloud.registration.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class RegistrationConfiguration {


    @Value("${web-service.url}")
    private String url;//正式

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.wondersgroup.healthcloud.wsdl");
        return marshaller;
    }

    @Bean
    public HospitalInfoClient weatherClient(Jaxb2Marshaller marshaller) {
        HospitalInfoClient client = new HospitalInfoClient();
        client.setDefaultUri(url);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public TopDeptInfoTopClient topDeptInfoTopClient(Jaxb2Marshaller marshaller) {
        TopDeptInfoTopClient client = new TopDeptInfoTopClient();
        client.setDefaultUri(url);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public DeptInfoTwoClient deptInfoTwoClient(Jaxb2Marshaller marshaller) {
        DeptInfoTwoClient client = new DeptInfoTwoClient();
        client.setDefaultUri(url);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public DoctInfoClient doctInfoClient(Jaxb2Marshaller marshaller) {
        DoctInfoClient client = new DoctInfoClient();
        client.setDefaultUri(url);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }


    @Bean
    public OrderClient orderClient(Jaxb2Marshaller marshaller) {
        OrderClient client = new OrderClient();
        client.setDefaultUri(url);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public UserInfoClient userInfoClient(Jaxb2Marshaller marshaller) {
        UserInfoClient client = new UserInfoClient();
        client.setDefaultUri(url);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

}
