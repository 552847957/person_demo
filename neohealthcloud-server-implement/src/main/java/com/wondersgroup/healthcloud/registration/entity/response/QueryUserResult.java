package com.wondersgroup.healthcloud.registration.entity.response;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by longshasha on 16/12/21.
 */
@XmlRootElement(name = "Result")
public class QueryUserResult{

    @XmlElementWrapper(name = "Users")
    @XmlElement(name = "user")
    public List<QueryUser> users;
}