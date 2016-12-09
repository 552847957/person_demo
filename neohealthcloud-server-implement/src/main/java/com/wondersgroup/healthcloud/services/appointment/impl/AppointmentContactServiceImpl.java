package com.wondersgroup.healthcloud.services.appointment.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentContact;
import com.wondersgroup.healthcloud.jpa.repository.appointment.ContactRepository;
import com.wondersgroup.healthcloud.registration.client.UserInfoClient;
import com.wondersgroup.healthcloud.registration.entity.request.*;
import com.wondersgroup.healthcloud.registration.entity.response.MemberInfoResultResponse;
import com.wondersgroup.healthcloud.registration.entity.response.UserInfoResultResponse;
import com.wondersgroup.healthcloud.services.appointment.AppointmentContactService;
import com.wondersgroup.healthcloud.services.appointment.exception.*;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.utils.registration.SignatureGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by longshasha on 16/12/8.
 * 预约挂号就诊人
 */
@Service
public class AppointmentContactServiceImpl implements AppointmentContactService {

    private static final Logger log = Logger.getLogger(AppointmentContactServiceImpl.class);

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserInfoClient userInfoClient;

    @Autowired
    private Environment environment;

    @Override
    public AppointmentContact addAppointmentContact(String uid, String name, String idcard, String mobile, String mediCardId) {

        String isDefault = "0";
        Boolean isFirstContact = false;
        if(name.length()>5){
            throw new ErrorAppointmentException("就诊人名字长度不能超过5");
        }
        if (!IdcardUtils.validateCard(idcard)) {
            throw new IdCardErrorException();
        }
        AppointmentContact appointmentContact = contactRepository.getAppointmentContactByIdCard(idcard);
        if (null != appointmentContact) {
            throw new ContactAlreadyExistException();
        }
        List<AppointmentContact> list = contactRepository.getAppointmentContactListByUid(uid);
        if (list.size() >= 3) {
            throw new ContactLimitException();
        }
        if(list == null || list.size()<1){
            isDefault = "1";//设置为默认
            isFirstContact = true;
        }

        //调用接口注册
        AppointmentContact contact = registerUserOrMember(uid, name, idcard, mobile,mediCardId,isFirstContact);

        contact.setId(IdGen.uuid());
        contact.setIsDefault(isDefault);
        contact.setCreateDate(new Date());
        contact.setUpdateDate(new Date());
        contact.setCreateBy(uid);
        contact.setUpdateBy(uid);

        contactRepository.save(contact);
        return contact;
    }


    /**
     * 获取就诊人列表
     * @param uid
     * @return
     */
    @Override
    public List<AppointmentContact> getAppointmentContactList(String uid) {

        List<AppointmentContact> list = contactRepository.getAppointmentContactListByUid(uid);
        return  list;
    }

    @Override
    public AppointmentContact getAppointmentContactById(String id) {
        return contactRepository.findOne(id);
    }

    /**
     * 根据用户Id 获取默认就诊人
     * @param uid
     * @return
     */
    @Override
    public AppointmentContact getDefaultAppointmentContactByUid(String uid) {

        return contactRepository.getDefaultAppointmentContactByUid(uid);
    }

    /**
     * 去注册预约挂号接口或者添加成员
     * @param uid
     * @param name
     * @param idcard
     * @param mobile
     * @param mediCardId
     * @return
     */
    private AppointmentContact registerUserOrMember(String uid, String name, String idcard,
                                                    String mobile, String mediCardId,Boolean isFirstContact) {

        AppointmentContact contact = new AppointmentContact(uid,name,idcard,mobile,mediCardId);
        if(isFirstContact){
            contact.setIsMain("1");

            UserInfoRequest userInfoRequest = new UserInfoRequest();
            userInfoRequest.requestMessageHeader = new RequestMessageHeader(environment);
            UserInfoR userInfoR = new UserInfoR();

            userInfoR.setOperType("0");
            userInfoR.setUserName(name);
            userInfoR.setUserCardType("01");
            userInfoR.setUserCardId(idcard);

            if(StringUtils.isNotBlank(mediCardId)){
                //0:无卡，初诊病人1：社保卡（医保卡）2：上海医联卡
                userInfoR.setMediCardIdType("1");
                userInfoR.setMediCardId(mediCardId);
            }

            userInfoR.setUserBD(IdcardUtils.getBirthStrByIdCard(contact.getIdcard()));
            userInfoR.setUserPhone(mobile);
            userInfoR.setUserSex(IdcardUtils.getGenderByIdCard(contact.getIdcard()));
            userInfoRequest.userInfoR = userInfoR;

            String sign = SignatureGenerator.generateSignature(userInfoRequest);
            userInfoRequest.requestMessageHeader.setSign(sign);

            String xmlRequest = JaxbUtil.convertToXml(userInfoRequest);
            UserInfoResultResponse userInfoResultResponse = userInfoClient.registerOrUpdateUserInfo(xmlRequest);

            if(userInfoResultResponse!=null&&"0".equals(userInfoResultResponse.messageHeader.getCode())){
                contact.setPlatformUserId(userInfoResultResponse.userInfoResult.getPlatformUserId());
                contact.setUserPwd(userInfoResultResponse.userInfoResult.getUserPwd());
            }else{
                log.error("registerOrUpdateUserInfo:code="+userInfoResultResponse.messageHeader.getCode()+",desc="+userInfoResultResponse.messageHeader.getDesc());
                throw new ErrorRegisteRegistrationUserException(userInfoResultResponse.messageHeader.getDesc());
            }
        }else{
            AppointmentContact mainContact = contactRepository.findMainContactByUid(uid);

            MemberInfoRequest memberInfoRequest = new MemberInfoRequest();
            memberInfoRequest.requestMessageHeader = new RequestMessageHeader(environment);
            MemberInfoR memberInfoR = new MemberInfoR();

            memberInfoR.setUserId(mainContact.getPlatformUserId());
            memberInfoR.setOperType("0");
            memberInfoR.setMemberName(name);
            memberInfoR.setPapersType("01");
            memberInfoR.setPapersNum(idcard);
            memberInfoR.setUserSex(IdcardUtils.getGenderByIdCard(contact.getIdcard()));
            memberInfoR.setUserPhone(mobile);
            memberInfoR.setUserState("0");//0-正常，1-注销
            if(StringUtils.isNotBlank(mediCardId)){
                //0:无卡，初诊病人1：社保卡（医保卡）2：上海医联卡
                memberInfoR.setMediCardIdType("1");
                memberInfoR.setMediCardId(mediCardId);
            }

            memberInfoRequest.memberInfoR = memberInfoR;
            String sign = SignatureGenerator.generateSignature(memberInfoRequest);
            memberInfoRequest.requestMessageHeader.setSign(sign);

            String xmlRequest = JaxbUtil.convertToXml(memberInfoRequest);
            MemberInfoResultResponse memberInfoResultResponse = userInfoClient.registerOrUpdateMemberInfo(xmlRequest);

            if(memberInfoResultResponse!=null&&"0".equals(memberInfoResultResponse.messageHeader.getCode())){
                contact.setMemberId(memberInfoResultResponse.memberInfoResult.getUserId());
            }else{
                log.error("registerOrUpdateUserInfo:code="+memberInfoResultResponse.messageHeader.getCode()+",desc="+memberInfoResultResponse.messageHeader.getDesc());
                throw new ErrorRegisteRegistrationUserException(memberInfoResultResponse.messageHeader.getDesc());
            }
        }

        return contact;
    }
}
