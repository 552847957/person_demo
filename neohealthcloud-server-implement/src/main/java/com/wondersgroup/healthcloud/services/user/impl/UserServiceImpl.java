package com.wondersgroup.healthcloud.services.user.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.AddressRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.UserInfoRepository;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.UserInfoForm;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUpdateGenderException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUpdateUserInfoException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUserAccountException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private JdbcTemplate jt;


    private String query = "select i.registerid ,i.`name`,i.nickname ,i.regmobilephone ,i.headphoto , " +
            " i.personcard ,i.gender ,i.identifytype ,i.talkid ,i.talkpwd ,i.tagid , " +
            " i.medicarecard ,i.bind_personcard ,ui.age ,ui.height , ui.weight , ui.waist " +
            " from app_tb_register_info i " +
            " left join app_tb_register_userinfo ui on i.registerid = ui.registerid ";

    @Override
    public Map<String, Object> findUserInfoByUid(String uid) {

        String sql = query + " where i.registerid = '%s'";
        sql = String.format(sql,uid);
        return jt.queryForMap(sql);
    }

    @Override
    public RegisterInfo getOneNotNull(String id) {
        RegisterInfo registerInfo = registerInfoRepository.findOne(id);
        if(registerInfo == null){
            throw new ErrorUserAccountException();
        }
        return registerInfo;
    }

    @Override
    public Boolean updateNickname(String userId, String nickname) {
        RegisterInfo register = registerInfoRepository.findOne(userId);
        register.setNickname(nickname);
        register.setUpdateDate(new Date());
        registerInfoRepository.saveAndFlush(register);
        return true;
    }

    @Override
    public Boolean updateGender(String userId, String gender) {
        RegisterInfo register = getOneNotNull(userId);
        if (register.verified()) {
            throw new ErrorUpdateGenderException("实名认证后性别不能修改");
        }
        register.setGender(gender);
        registerInfoRepository.saveAndFlush(register);

        return true;
    }

    @Override
    public RegisterInfo findRegisterInfoByMobile(String mobile) {
        RegisterInfo register = registerInfoRepository.findByMobile(mobile);
        return register;
    }

    @Transactional
    @Override
    public void updateUserInfo(UserInfoForm form) {
        RegisterInfo registerInfo = getOneNotNull(form.registerId);
        if(registerInfo.verified() && form.age !=null){
            throw new ErrorUpdateUserInfoException("实名认证后不能修改年龄");
        }
        if(registerInfo.verified() && StringUtils.isNotBlank(form.gender)){
            throw new ErrorUpdateUserInfoException("实名认证后不能修改性别");
        }

        UserInfo userInfo = userInfoRepository.findOne(form.registerId);
        if (userInfo == null){
            userInfo = new UserInfo();
            userInfo.setRegisterid(form.registerId);
            userInfo.setDelFlag("0");
        }
        UserInfo merged = form.merge(userInfo);

        userInfoRepository.saveAndFlush(merged);

        if(StringUtils.isNotBlank(form.gender)){
            registerInfo.setGender(form.gender);
            registerInfoRepository.saveAndFlush(registerInfo);
        }

    }

    @Override
    public UserInfo getUserInfo(String uid) {
        UserInfo userInfo = userInfoRepository.findOne(uid);
        return userInfo;
    }

    @Override
    public void updateAvatar(String uid, String avatar) {
        RegisterInfo register = getOneNotNull(uid);
        register.setHeadphoto(avatar);
        registerInfoRepository.saveAndFlush(register);
    }

    @Override
    public Address updateAddress(String userId, String province, String city, String county, String town, String committee, String other) {
        Address address = getAddress(userId);
        Date date = new Date();
        if (address == null) {
            address = new Address();
            address.setUserId(userId);
            address.setId(IdGen.uuid());
            address.setDelFlag("0");
            address.setCreateBy(userId);
            address.setCreateDate(date);
        }
        address.setProvince(province);
        address.setCity(city);
        address.setCounty(county);
        address.setTown(town);
        address.setCommittee(committee);
        address.setOther(other);
        address.setUpdateBy(userId);
        address.setUpdateDate(date);
        return addressRepository.save(address);
    }

    @Override
    public Address getAddress(String userId) {
        List<Address> addressList = addressRepository.findByUserId(userId);
        return addressList.isEmpty() ? null : addressList.get(0);
    }
}
