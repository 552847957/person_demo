package com.wondersgroup.healthcloud.services.user.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.OkHttpClient;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.common.utils.JailPropertiesUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.AddressRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.UserInfoRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.UserInfoForm;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUpdateGenderException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUpdateUserInfoException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUserAccountException;
import com.wondersgroup.healthcloud.utils.InterfaceEnCode;
import com.wondersgroup.healthcloud.utils.familyDoctor.FamilyDoctorUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
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
    private DoctorService doctorService;

    @Autowired
    private JdbcTemplate jt;

    @Autowired
    private JailPropertiesUtils jailPropertiesUtils;



    private String query = "select i.registerid ,i.`name`,i.nickname ,i.regmobilephone ,i.headphoto , " +
            " i.personcard ,i.gender ,i.identifytype ,i.talkid ,i.talkpwd ,i.tagid , " +
            " i.medicarecard ,i.bind_personcard ,ui.age ,ui.height , ui.weight , ui.waist " +
            " from app_tb_register_info i " +
            " left join app_tb_register_userinfo ui on i.registerid = ui.registerid ";

    @Override
    public Map<String, Object> findUserInfoByUid(String uid) {

        String sql = query + " where i.registerid = '%s'";
        sql = String.format(sql, uid);
        try {
            return jt.queryForMap(sql);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public RegisterInfo getOneNotNull(String id) {
        RegisterInfo registerInfo = registerInfoRepository.findOne(id);
        if (registerInfo == null) {
            throw new ErrorUserAccountException();
        }
        return registerInfo;
    }

    @Override
    public List<RegisterInfo> findRegisterInfoByIdcard(String idcard) {
        return registerInfoRepository.findByPersoncard(idcard);
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
        if (registerInfo.verified() && form.age != null) {
            throw new ErrorUpdateUserInfoException("实名认证后不能修改年龄");
        }
        if (registerInfo.verified() && StringUtils.isNotBlank(form.gender)) {
            throw new ErrorUpdateUserInfoException("实名认证后不能修改性别");
        }

        UserInfo userInfo = userInfoRepository.findOne(form.registerId);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setRegisterid(form.registerId);
            userInfo.setDelFlag("0");
        }
        UserInfo merged = form.merge(userInfo);

        userInfoRepository.saveAndFlush(merged);

        if (StringUtils.isNotBlank(form.gender)) {
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


    @Override
    public Map<String, Object>  findSignDoctorByUid(String uid) {
        Map<String,Object> doctorInfor = new HashMap<>();
        String doctorIdcard = "";
        RegisterInfo account = getOneNotNull(uid);
        if(account.verified() && StringUtils.isNotBlank(account.getPersoncard())){
            JsonNode result = getFamilyDoctorByUserPersoncard(account.getPersoncard());
            if(result.get("code").asInt()==0){
                doctorIdcard = result.get("data").get("personcard")==null?"":result.get("data").get("personcard").asText();
                if(StringUtils.isNotBlank(doctorIdcard))
                    doctorInfor = doctorService.findDoctorInfoByIdcard(doctorIdcard);
            }
        }
        return doctorInfor;
    }

    @Override
    public JsonNode getFamilyDoctorByUserPersoncard(String personcard){
        FamilyDoctorUtil familyDoctorUtil = new FamilyDoctorUtil();
        familyDoctorUtil.setHttpRequestExecutorManager(new HttpRequestExecutorManager(new OkHttpClient()));
        JsonNode result = familyDoctorUtil.getFamilyDoctorByUserPersoncard(jailPropertiesUtils.getGwWebSignedUrl(),personcard);
        return result;
    }



    //----------------------后台使用----------------------


    @Override
    public List<Map<String, Object>> findUserListByPager(int pageNum, int size, Map parameter) {

        String sqlQuery = "SELECT registerid,userid,`name`,nickname,gender,regmobilephone,identifytype,personcard,regtime,GROUP_CONCAT(tagname) as  tagList " +
                "from (select r.registerid,r.userid,r.`name`,r.nickname,r.gender,r.regmobilephone,r.identifytype,r.personcard " +
                ",tag.tagid,dt.tagname,r.regtime  " +
                " from app_tb_register_info r " +
                "left join app_tb_tag_user tag on r.registerid = tag.registerid " +
                "left join app_tb_push_tag dt on tag.tagid = dt.tagid "+getTagListLike(parameter)+" ) d ";

        String sql = sqlQuery + " where 1=1 "+
                getWhereSqlByParameter(parameter)
                + " group by registerid "
                + " ORDER BY regtime  desc "
                + " LIMIT " +(pageNum-1)*size +"," + size;
        return jt.queryForList(sql);

    }

    @Override
    public int countUserByParameter(Map parameter) {
        String sqlQuery = "SELECT count(DISTINCT(registerid)) " +
                "from (select r.registerid,r.userid,r.`name`,r.nickname,r.gender,r.regmobilephone,r.identifytype,r.personcard " +
                ",tag.tagid,dt.tagname,r.create_date  " +
                " from app_tb_register_info r " +
                "left join app_tb_tag_user tag on r.registerid = tag.registerid " +
                "left join app_tb_push_tag dt on tag.tagid = dt.tagid "+getTagListLike(parameter)+" ) d ";
        String sql = sqlQuery + " where 1=1 "+
                getWhereSqlByParameter(parameter) ;
        Integer count = jt.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public Map<String, Object> findUserDetailByUid(String registerid) {
        String sqlQuery = "SELECT registerid,userid,`name`,headphoto,nickname,gender,regmobilephone,identifytype,personcard,regtime,GROUP_CONCAT(tagname) as  tagList " +
                "from (select r.registerid,r.userid,r.`name`,r.headphoto,r.nickname,r.gender,r.regmobilephone,r.identifytype,r.personcard " +
                ",tag.tagid,dt.tagname,r.regtime  " +
                " from app_tb_register_info r " +
                "left join app_tb_tag_user tag on r.registerid = tag.registerid " +
                "left join app_tb_push_tag dt on tag.tagid = dt.tagid ) d ";
        String sql = sqlQuery + " where d.registerid = '%s' group by registerid";
        sql = String.format(sql, registerid);
        try {
            return jt.queryForMap(sql);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public Boolean updateMedicarecard(String uid, String medicareCard) {
        RegisterInfo register = getOneNotNull(uid);
        if (!register.verified()) {
            throw new CommonException(1020, "非实名认证不能绑定医保卡");
        }
        if (null != register.getMedicarecard() && register.getMedicarecard().length()>0){
            throw new CommonException(1021, "已绑定医保卡");
        }
        register.setMedicarecard(medicareCard);
        registerInfoRepository.saveAndFlush(register);
        return true;
    }

    private String getTagListLike(Map parameter){
        StringBuffer bf = new StringBuffer();
        if(parameter.size()>0 && parameter.containsKey("tagList") &&  StringUtils.isNotBlank(parameter.get("tagList").toString())){
            String tagList = parameter.get("tagList").toString();
            String[] tags = tagList.split(",");
            bf.append(" where  ");
            int size = tags.length;
            int i = 0;
            for (String tag :tags){
                i++;
                if(i<size){
                    bf.append(" dt.tagname like '%"+tag+"%' or ");
                }else{
                    bf.append(" dt.tagname like '%"+tag+"%'  ");
                }

            }
        }
        return bf.toString();
    }
    private String getWhereSqlByParameter(Map parameter) {
        StringBuffer bf = new StringBuffer();
        if(parameter.size()>0){
            if(parameter.containsKey("nickname") &&  StringUtils.isNotBlank(parameter.get("nickname").toString())){
                bf.append(" and d.nickname like '%"+parameter.get("nickname").toString()+"%' ");
            }
            if(parameter.containsKey("name") && StringUtils.isNotBlank(parameter.get("name").toString())){
                bf.append(" and d.name like '%"+parameter.get("name").toString()+"%' ");
            }
            if(parameter.containsKey("regmobilephone") && StringUtils.isNotBlank(parameter.get("regmobilephone").toString())){
                bf.append(" and d.regmobilephone like '%"+parameter.get("regmobilephone").toString()+"%' ");
            }
            if(parameter.containsKey("personcard") && StringUtils.isNotBlank(parameter.get("personcard").toString())){
                bf.append(" and d.personcard like '%"+parameter.get("personcard").toString()+"%' ");
            }
            if(parameter.containsKey("userid") && StringUtils.isNotBlank(parameter.get("userid").toString())){
                bf.append(" and d.userid like '%"+parameter.get("userid").toString()+"%' ");
            }
        }
        return bf.toString();
    }
}
