package com.wondersgroup.healthcloud.services.doctor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceRoleMap;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorServiceRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorServiceRoleMapRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorSyncAccountService;
import com.wondersgroup.healthcloud.services.doctor.exception.SyncDoctorAccountException;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import com.wondersgroup.healthcloud.utils.easemob.EasemobAccount;
import com.wondersgroup.healthcloud.utils.easemob.EasemobDoctorPool;
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;
import com.wondersgroup.healthcloud.utils.wonderCloud.RSAUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by longshasha on 16/8/2.
 */
@Service
public class DoctorSyncAccountServiceimpl implements DoctorSyncAccountService {

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    @Autowired
    private DoctorServiceRoleMapRepository roleMapRepository;

    @Autowired
    private DoctorServiceRepository doctorServiceRepository;


    @Autowired
    private EasemobDoctorPool easemobDoctorPool;

    @Autowired
    private HttpWdUtils httpWdUtils;



    @Override
    public DoctorAccount findDoctorByMobileWithOutDelfag(String mobile) {
        return doctorAccountRepository.findDoctorByMobileWithOutDelfag(mobile);
    }

    /**
     * 开通万达云账号
     * @param doctorAccount
     * @param doctorInfo
     * @param roles
     * @return
     */
    @Override
    @Transactional
    public DoctorAccount openWonderCloudAccount(DoctorAccount doctorAccount, DoctorInfo doctorInfo,String roles) {

        //根据手机号查询万达云账号
        JsonNode result = httpWdUtils.basicInfo(doctorAccount.getMobile());

        String registerId = "";
        String loginName = "";
        Boolean success = result.get("success").asBoolean();
        if(!success){
            try {
                String psw = RSAUtil.encryptByPublicKey("initPwd2016", httpWdUtils.publicKey);
            }catch (Exception e){
                throw new SyncDoctorAccountException(e.getLocalizedMessage());
            }
            JsonNode jsonNode =  httpWdUtils.registe(doctorAccount.getMobile(),psw);
            Boolean isRegisteSuccess = jsonNode.get("success").asBoolean();
            if (isRegisteSuccess) {
                registerId = jsonNode.get("userid").asText();
            }else{
                throw new SyncDoctorAccountException("万达云账号注册失败,"+jsonNode.get("msg").asText());
            }

        }else{
            registerId = result.get("user").get("userid").asText();
            loginName = result.get("user").get("username").asText();
        }

        DoctorAccount account = doctorAccountRepository.findOne(registerId);
        if(account!=null && "0".equals(account.getDelFlag())){
            throw new SyncDoctorAccountException("已存在一个registerId相同的数据,手机号为:"+account.getMobile());
        }

        if(StringUtils.isBlank(doctorAccount.getTalkid())){
            EasemobAccount easemobAccount = easemobDoctorPool.fetchOne();
            if (easemobAccount!=null) {//注册环信
                doctorAccount.setTalkid(easemobAccount.id);
                doctorAccount.setTalkpwd(easemobAccount.pwd);
            }
        }


        doctorAccount.setId(registerId);
        if(StringUtils.isNotBlank(loginName)){
            doctorAccount.setLoginName(loginName);
        }
        doctorAccount.setIsAvailable("1");
        doctorAccount.setDelFlag("0");
        doctorAccount.setUpdateDate(new Date());
        doctorAccountRepository.saveAndFlush(doctorAccount);

        DoctorInfo oldDoctorInfo = doctorInfoRepository.findOne(registerId);

        if(oldDoctorInfo == null){
            oldDoctorInfo = new DoctorInfo();
            oldDoctorInfo.setId(registerId);
            oldDoctorInfo.setCreateDate(new Date());

        }
        oldDoctorInfo.setHospitalId(doctorInfo.getHospitalId());
        oldDoctorInfo.setNo(doctorInfo.getNo());
        oldDoctorInfo.setIdcard(doctorInfo.getIdcard());
        if(StringUtils.isNotBlank(doctorInfo.getIdcard())){
            oldDoctorInfo.setGender(IdcardUtils.getGenderByIdCard(doctorInfo.getIdcard()));
        }
        oldDoctorInfo.setDelFlag("0");
        oldDoctorInfo.setDutyId(doctorInfo.getDutyId());
        oldDoctorInfo.setUpdateDate(new Date());
        doctorInfoRepository.saveAndFlush(oldDoctorInfo);

        if(StringUtils.isNotBlank(roles)){
            //设置医生对应的服务包 根据roles 来判断医生拥有哪些服务包
            String[] roleStr = roles.split(",");
            Collection<String> roleList = Lists.newArrayList();
            for(String role : roleStr){
                roleList.add(role);
            }
            List<DoctorServiceRoleMap> serviceRoleMaps = roleMapRepository.findServicesByRoles(roleList);
            if(serviceRoleMaps.size()>0){
                for(DoctorServiceRoleMap roleMap : serviceRoleMaps){
                    DoctorServiceEntity doctorServiceEntity = new DoctorServiceEntity();
                    doctorServiceEntity.setId(IdGen.uuid());
                    doctorServiceEntity.setDoctorId(doctorAccount.getId());
                    doctorServiceEntity.setServiceId(roleMap.getServiceId());
                    doctorServiceEntity.setDelFlag("0");
                    doctorServiceEntity.setCreateDate(new Date());
                    doctorServiceEntity.setUpdateDate(new Date());
                    doctorServiceRepository.saveAndFlush(doctorServiceEntity);
                }
            }
        }
        return doctorAccount;
    }

    /**
     * 接触万达云账号
     * @param registerId
     */
    @Override
    public void closeWonderCloudAccount(String registerId) {

        DoctorAccount doctorAccount = doctorAccountRepository.getOne(registerId);
        if(doctorAccount==null || "1".equals(doctorAccount.getDelFlag())){
            throw new SyncDoctorAccountException("该账号没有开通万达云账号");
        }

        doctorAccountRepository.closeWonderCloudAccount(registerId);
        doctorInfoRepository.closeWonderCloudAccount(registerId);

        //物理删除医生对应的服务
        doctorServiceRepository.removeServiceByUid(registerId);

    }

    @Override
    public DoctorInfo findDoctorByPersoncardWithOutDelflag(String idcard) {

        return doctorInfoRepository.findDoctorByPersoncardWithOutDelflag(idcard);
    }

    @Override
    public DoctorAccount findDoctorById(String id) {
        return doctorAccountRepository.findOne(id);
    }


}
