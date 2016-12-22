package com.wondersgroup.healthcloud.registration.entity.response;



import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/12/21.
 */
@XmlRootElement(name = "member")
public class QueryMember {

    public String memberName;
    public String memberCardType;
    public String memberCardId;
    public String memberSex;
    public String memberId;
    public String memberPhone;
    public String memberMediCardId;
    public String mediCardIdType;

}
