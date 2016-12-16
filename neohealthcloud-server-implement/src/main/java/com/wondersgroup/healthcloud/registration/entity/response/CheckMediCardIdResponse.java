package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by longshasha on 16/11/15.
 */
@XmlRootElement(name = "Response")
public class CheckMediCardIdResponse extends BaseResponse {

    @XmlElement(name = "CardInfo")
    public List<CheckMediCardId> cardInfo;
}
