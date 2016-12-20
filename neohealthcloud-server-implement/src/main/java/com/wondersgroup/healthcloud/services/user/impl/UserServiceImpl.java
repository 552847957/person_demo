package com.wondersgroup.healthcloud.services.user.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.OkHttpClient;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.common.utils.JailPropertiesUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.AddressRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.AnonymousAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.UserInfoRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.UserInfoForm;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUpdateGenderException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUpdateUserInfoException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUserAccountException;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.familyDoctor.FamilyDoctorUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private RegisterInfoRepository registerInfoRepository;
    
    @Autowired
    private AnonymousAccountRepository anonymousAccountRepository;
    
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

    @Value("${internal.api.service.measure.url}")
    private String measureUrl;



    private String query = "select i.registerid ,i.`name`,i.nickname ,i.regmobilephone ,i.headphoto , " +
            " i.personcard ,i.gender ,i.identifytype ,i.talkid ,i.talkpwd ,i.tagid, i.identifytype , " +
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
    public RegisterInfo findOne(String id) {
        return registerInfoRepository.findOne(id);
    }

    @Override
    public Map<String, RegisterInfo> findByUids(Iterable<String> uids) {
        List<RegisterInfo> list = registerInfoRepository.findAll(uids);
        if (null == list || list.isEmpty()){
            return null;
        }
        Map<String, RegisterInfo> map = new HashMap<>();
        for (RegisterInfo registerInfo : list){
            map.put(registerInfo.getRegisterid(), registerInfo);
        }
        return map;
    }

    @Override
    public List<RegisterInfo> findRegisterInfoByIdcard(String idcard) {
        return registerInfoRepository.findByPersoncard(idcard);
    }

    @Override
    public Boolean updateNickname(String userId, String nickname) {
        return updateNicknameAndAvatar(userId, nickname, null);
    }

    @Override
    public Boolean updateNicknameAndAvatar(String userId, String nickname, String avatar) {
        //根据昵称查询用户数量
        Boolean isUsedNickName = registerInfoRepository.checkNickNameisUsedIgnoreAppointUid(nickname, userId);
        if(isUsedNickName){
            throw new ErrorUpdateUserInfoException("昵称已被使用哦,换一个吧。");
        }
        RegisterInfo register = registerInfoRepository.findOne(userId);
        register.setNickname(nickname);
        if (StringUtils.isNotEmpty(avatar)){
            register.setHeadphoto(avatar);
        }
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

        //修改BMI
        /*if(form.height!=null || form.weight !=null){
            updateBMI(userInfo);
        }*/

        if (StringUtils.isNotBlank(form.gender)) {
            registerInfo.setGender(form.gender);
            registerInfoRepository.saveAndFlush(registerInfo);
        }

    }

    @Transactional
    @Override
    public Boolean updateUserHeightAndWeight(UserInfoForm form) {

        UserInfo userInfo = userInfoRepository.findOne(form.registerId);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setRegisterid(form.registerId);
            userInfo.setDelFlag("0");
        }
        UserInfo merged = form.merge(userInfo);

        try {
            userInfoRepository.saveAndFlush(merged);
        }catch (Exception e){
            log.error("UserServiceImpl.updateUserHeightAndWeight Error -->" + e.getLocalizedMessage());
            return false;
        }


        return true;

    }

    private void updateBMI(UserInfo userInfo) {
        String url = measureUrl + "/api/measure/upload/0";
        Date date = new Date();
        String testTime = DateFormatter.dateTimeFormat(date);
        Map<String, Object> paras = new HashMap<>();
        paras.put("registerId",userInfo.getRegisterid());
        paras.put("height",userInfo.getHeight());
        paras.put("weight",userInfo.getWeight());
        paras.put("measureWay","1");
        paras.put("testTime",testTime);
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("access-token", "version3.0");
        ResponseEntity<Map> response = template.postForEntity(url, new HttpEntity<>(paras, headers), Map.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            if (0 != (int) response.getBody().get("code")) {
                log.error("UserServiceImpl.updateBMI Error  -->"+response.getBody().get("msg"));
            }
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

    public Boolean getInvitationActived(String userId) {
        List<Map<String, Object>> maps = jt.queryForList(String.format("select 0 from app_tb_invitation where uid='%s' limit 1", userId));
        return !maps.isEmpty();
    }

    @Override
    public void activeInvitation(String uid, String code) {
        if (getInvitationActived(uid)) {
            throw new CommonException(1071, "已经激活过, 不能重复激活");
        }
        Doctor doctorInfo = doctorService.findDoctorInfoByActcode(code);
        if (doctorInfo == null) {
            throw new CommonException(1070, "我知道你在开玩笑，但邀请码还是要输对哦");
        } else {
            String doctorId = doctorInfo.getUid();
            int rowsAffected = jt.update(String.format("insert app_tb_invitation(id, uid, doctorid, create_date) values('%s','%s','%s','%s')", IdGen.uuid(),uid, doctorId, DateFormatter.dateTimeFormat(new Date())));
        }
    }




    //----------------------后台使用----------------------
    @Override
    public List<Map<String, Object>> findUserListByPager(int pageNum, int size, Map parameter) {

        String sqlQuery = " select r.registerid,r.userid,r.`name`,r.nickname,r.gender,r.regmobilephone,r.identifytype," +
                "  r.personcard ,tag.tagid,dt.tagname,r.regtime,GROUP_CONCAT(dt.tagname) as tagList " +
                "  from app_tb_register_info r " +
                "  left join app_tb_tag_user tag on r.registerid = tag.registerid " +
                "  left join app_tb_push_tag dt on tag.tagid = dt.tagid ";

        String sql = sqlQuery + " where 1=1 "+
                getWhereSqlByParameter(parameter)+
                getTagListLike(parameter)+
                " group by r.registerid  "+
                " ORDER BY r.regtime  desc "+
                " LIMIT " +(pageNum-1)*size +"," + size;
        return jt.queryForList(sql);

    }

    @Override
    public int countUserByParameter(Map parameter) {
        String sqlQuery = " select  count(DISTINCT(r.registerid))  " +
                "  from app_tb_register_info r " +
                "  left join app_tb_tag_user tag on r.registerid = tag.registerid " +
                "  left join app_tb_push_tag dt on tag.tagid = dt.tagid ";

        String sql = sqlQuery + " where 1=1 "+
                getWhereSqlByParameter(parameter)+
                getTagListLike(parameter) ;
        Integer count = jt.queryForObject(sql, Integer.class);

        return count == null ? 0 : count;
    }

    @Override
    public Map<String, Object> findUserDetailByUid(String registerid) {
        String sqlQuery = " select r.registerid,r.userid,r.`name`,r.nickname,r.gender,r.regmobilephone,r.identifytype," +
                "  r.personcard ,tag.tagid,dt.tagname,r.regtime,GROUP_CONCAT(dt.tagname) as tagList " +
                "  from app_tb_register_info r " +
                "  left join app_tb_tag_user tag on r.registerid = tag.registerid " +
                "  left join app_tb_push_tag dt on tag.tagid = dt.tagid ";

        String sql = sqlQuery + " where r.registerid = '%s' group by r.registerid";
        sql = String.format(sql, registerid);
        try {
            return jt.queryForMap(sql);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public String findFirstTagName() {
        String tagname = "请至少填写一个条件";
        Map<String,Object> map = new HashMap<>();
        String sql = "select tagname from app_tb_push_tag  order by updatetime desc limit 1";
        try {
            map = jt.queryForMap(sql);
            if(map!=null)
                tagname = map.get("tagname")==null?tagname:map.get("tagname").toString();
        }catch (EmptyResultDataAccessException e){
            return tagname;
        }
        return tagname;
    }

    public RegisterInfo findRegOrAnonymous(String registerId){
        RegisterInfo info = findOne(registerId);
        if(info == null){
            info = new RegisterInfo();
            AnonymousAccount ac = anonymousAccountRepository.findOne(registerId);
            info.setRegisterid(registerId);
            info.setHeadphoto(ac.getHeadphoto());
            info.setBirthday(ac.getBirthDate());
            info.setGender(ac.getSex());
            info.setPersoncard(ac.getIdcard());
            info.setRegmobilephone(ac.getMobile());
            info.setNickname(ac.getNickname());
            info.setName(ac.getName());
        }
        return info;
        
    }


    private String getTagListLike(Map parameter){
        StringBuffer bf = new StringBuffer();
        if(parameter.size()>0 && parameter.containsKey("tagList") &&  StringUtils.isNotBlank(parameter.get("tagList").toString())){
            String tagList = parameter.get("tagList").toString();
            String[] tags = tagList.split(",");
            bf.append(" and  ");
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
                bf.append(" and r.nickname like '%"+parameter.get("nickname").toString()+"%' ");
            }
            if(parameter.containsKey("name") && StringUtils.isNotBlank(parameter.get("name").toString())){
                bf.append(" and r.name like '%"+parameter.get("name").toString()+"%' ");
            }
            if(parameter.containsKey("regmobilephone") && StringUtils.isNotBlank(parameter.get("regmobilephone").toString())){
                bf.append(" and r.regmobilephone like '%"+parameter.get("regmobilephone").toString()+"%' ");
            }
            if(parameter.containsKey("personcard") && StringUtils.isNotBlank(parameter.get("personcard").toString())){
                bf.append(" and r.personcard like '%"+parameter.get("personcard").toString()+"%' ");
            }
            if(parameter.containsKey("userid") && StringUtils.isNotBlank(parameter.get("userid").toString())){
                bf.append(" and r.userid like '%"+parameter.get("userid").toString()+"%' ");
            }
        }
        return bf.toString();
    }
}
