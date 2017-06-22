package com.wondersgroup.healthcloud.services.group.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.constant.CommonConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.AopInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.group.PatientGroup;
import com.wondersgroup.healthcloud.jpa.entity.group.SignUserDoctorGroup;
import com.wondersgroup.healthcloud.jpa.repository.group.PatientGroupRepository;
import com.wondersgroup.healthcloud.jpa.repository.group.SignUserDoctorGroupRepository;
import com.wondersgroup.healthcloud.services.group.PatientGroupService;
import com.wondersgroup.healthcloud.utils.EmojiUtils;

@Service("patientGroupService")
public class PatientGroupServiceImpl implements PatientGroupService{
    public static Integer DEFAULT_SORT=1;
    public static String notDelFlag="0";
    public static String delFlag="1";
    @Autowired
    PatientGroupRepository patientGroupRepository;
    @Autowired
    SignUserDoctorGroupRepository signUserDoctorGroupRepository;

    @Override
    public List<PatientGroup> getPatientGroupByDoctorId(String doctorId) {
        List<PatientGroup> list = patientGroupRepository.getPatientGroupByDoctorId(doctorId);
        if(CollectionUtils.isEmpty(list)){
            PatientGroup group = new PatientGroup();
            group.setName("默认分组");
            group.setDoctorId(doctorId);
            group.setDelFlag("0");
            group.setIsDefault("1");
            group.setRank(DEFAULT_SORT);
            group.setCreateTime(new Date());
            patientGroupRepository.saveAndFlush(group);
            return patientGroupRepository.getPatientGroupByDoctorId(doctorId);
        }
        return list;
    }

    @Override
    public String savePatientGroup(String id,String doctorId, String name) {
        List<PatientGroup> list = patientGroupRepository.getPatientGroupByDoctorId(doctorId);
        if(CollectionUtils.isNotEmpty(list)&&list.size()>=20&&StringUtils.isBlank(id)){
            throw new CommonException(1041, "分组已超过20个,无法继续创建");
        }
        if(StringUtils.trim(name)==null||"".equals(StringUtils.trim(name))){
            throw new CommonException(1042,"分组名称不支持空白");
        }
        if(StringUtils.isNotBlank(name)&&name.length()>12){
            throw new CommonException(1048,"分组名称长度不能超过12个字符");
        }
        if(EmojiUtils.containsEmoji(name)){
            throw new CommonException(1043, "分组名称不支持表情符号") ;
        }
        PatientGroup isRepeatedName = patientGroupRepository.findIsNameRepeated(doctorId, name);
        //对原有的分组名称不变进行update
        if(StringUtils.isNotBlank(id)&&null!=isRepeatedName&&StringUtils.isNotBlank(isRepeatedName.getName())&&name.equals(isRepeatedName.getName())){
            throw new CommonException(1049, "编辑分组成功");
        }else if(null!=isRepeatedName&&StringUtils.isNotBlank(isRepeatedName.getName())&&name.equals(isRepeatedName.getName())){
            throw new CommonException(1044,"分组名称不支持重复");
        }
        PatientGroup group = new PatientGroup();
        if(StringUtils.isNotBlank(id)){
            PatientGroup one = patientGroupRepository.findOne(Integer.parseInt(id));
            one.setUpdateTime(new Date());
            one.setCreateTime(one.getUpdateTime());
            one.setDoctorId(doctorId);
            one.setName(StringUtils.trim(name));
            patientGroupRepository.save(one);
            return "编辑分组成功";
        }else{
            int maxSort=0;
            try {
                maxSort = patientGroupRepository.getMaxSortByDoctorId(doctorId);
            } catch (AopInvocationException e) {
                throw new CommonException(1047,"医生id不存在");
            }
            group.setDoctorId(doctorId);
            group.setName(StringUtils.trim(name));
            group.setRank(++maxSort);
            group.setIsDefault("0");
            group.setCreateTime(new Date());
            patientGroupRepository.save(group);
            return "新建分组成功";
        }
    }

    @Transactional
    @Override
    public Boolean delPatientGroup(String id, String doctorId) {
        PatientGroup findOne = patientGroupRepository.getByIdAndDoctorId(Integer.parseInt(id),doctorId);
        if(findOne==null){
            throw new CommonException(1044,"分组不存在");
        }
        String isDefault = findOne.getIsDefault();
        if("1".equals(isDefault)&&StringUtils.isNotBlank(isDefault)){
            throw new CommonException(1045,"默认分组不能删除"); 
        }
        PatientGroup one = patientGroupRepository.findOne(Integer.parseInt(id));
        one.setId(Integer.parseInt(id));
        one.setDelFlag("1");
        one.setUpdateTime(new Date());
        patientGroupRepository.save(one);
        signUserDoctorGroupRepository.updateDoctorGroup(delFlag,Integer.parseInt(id));
        return true;
        
    }
    
    @Transactional
    @Override
    public void sortPatientGroup(List<String> sortIds, String doctorId) {
        PatientGroup group=null;
        int sort = 0;
        for(String id:sortIds){
            group = patientGroupRepository.findOne(Integer.parseInt(id));
            group.setId(Integer.parseInt(id));
            group.setUpdateTime(new Date());
            group.setRank(++sort);
            patientGroupRepository.save(group);
        }
        
    }

    @Override
    public int getGroupNumByDoctorId(String doctorId) {
        return patientGroupRepository.getGroupNumByDoctorId(doctorId);
    }

    @Transactional
    @Override
    public void addUserToGroup(List<String> groupIds, String userId,String doctorId) {
        //已分组的id
        List<Integer> list = signUserDoctorGroupRepository.getGroupIdsByUserId(userId,doctorId);
        //传入的分组id
        List<Integer> list2 = null;
        if(CollectionUtils.isNotEmpty(groupIds)){
            list2 = CollStringToIntegerLst(groupIds);
            //取差集
            //list2.removeAll(list);
            list.removeAll(list2);
            for(Integer groupId:list){
                signUserDoctorGroupRepository.updateDoctorGroup(delFlag,groupId,userId);
            }
        }else{
            //传入的ids是空时,将原有的进行删除
            for(Integer groupId:list){
                signUserDoctorGroupRepository.updateDoctorGroup(delFlag,groupId,userId);
            }
        }
        if(CollectionUtils.isNotEmpty(list2)){
            List<Integer> reList = signUserDoctorGroupRepository.getGroupIdsByUserId(userId,doctorId);
            list2.removeAll(reList);
            for(Integer groupId:list2){
                SignUserDoctorGroup userDoctorGroup = signUserDoctorGroupRepository.getIsSelectedByGroupIdAndUserId(userId, groupId, delFlag);
                if(null!=userDoctorGroup){
                    signUserDoctorGroupRepository.updateDoctorGroup(notDelFlag, groupId,userId);
                }else{
                    SignUserDoctorGroup signUserDoctorGroup = new SignUserDoctorGroup();
                    signUserDoctorGroup.setGroupId(groupId);
                    signUserDoctorGroup.setUid(userId);
                    signUserDoctorGroup.setDelFlag("0");
                    signUserDoctorGroup.setCreateTime(new Date());
                    signUserDoctorGroupRepository.saveAndFlush(signUserDoctorGroup);
                }
            }
        }
    }

    @Override
    public List<String> getUserIdsByGroupId(Integer groupId) {
        List<SignUserDoctorGroup> groupList = signUserDoctorGroupRepository.queryByDelFlagAndGroupIdOrderByCreateTimeAsc(CommonConstant.USED_DEL_FLAG, groupId);
        List<String> userIdList = Lists.newArrayList();
        if (groupList != null && groupList.size() > 0) {
            for (SignUserDoctorGroup signUserDoctorGroup : groupList) {
                userIdList.add(signUserDoctorGroup.getUid());
            }
        }
        return userIdList;
    }

    /**
     * List<String> to List<Integer>
     * @param inList
     * @return
     */
    public static List<Integer> CollStringToIntegerLst(List<String> inList){
        List<Integer> iList =new ArrayList<Integer>(inList.size());
        CollectionUtils.collect(inList, 
                  new Transformer(){
                    public java.lang.Object transform(java.lang.Object input){
                      return new Integer((String)input);
                    }
                  } ,iList );
        return iList;
    }
    
}
