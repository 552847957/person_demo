package com.wondersgroup.healthcloud.registration.entity.response;



import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by longshasha on 16/12/21.
 */
@XmlRootElement(name = "user")
public class QueryUser {
    public String userCardType;
    public String userId;
    public String mediCardIdType;
    public String userSex;
    public String userCardId;
    public String userName;
    public String mediCardId;
    public String userBD;
    public String userPhone;
    public String userContAdd;

    @XmlElementWrapper(name = "Members")
    @XmlElement(name = "member")
    public List<QueryMember> members;

}
