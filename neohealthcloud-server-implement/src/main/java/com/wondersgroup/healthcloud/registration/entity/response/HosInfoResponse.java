package com.wondersgroup.healthcloud.registration.entity.response;



import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by longshasha on 16/5/22.
 */
@XmlRootElement(name = "Response")
public class HosInfoResponse extends BaseResponse{

    @XmlElementWrapper(name = "List")
    @XmlElement(name = "Result")
    public List<HosInfo> hosInfoList;

}
