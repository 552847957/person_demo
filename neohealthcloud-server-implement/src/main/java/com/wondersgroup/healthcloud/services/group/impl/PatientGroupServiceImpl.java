package com.wondersgroup.healthcloud.services.group.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;
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
        if(CollectionUtils.isNotEmpty(list)&&list.size()>=20){
            throw new CommonException(1041, "分组已超过20个,无法继续创建");
        }
        if(StringUtils.trim(name)==null||"".equals(StringUtils.trim(name))){
            throw new CommonException(1042,"分组名称不支持空白"); 
        }
        String cleanName=EmojiUtils.cleanEmoji(name);
        if(name.length()>cleanName.length()){
            throw new CommonException(1043, "分组名称不支持表情符号") ;
        }
        PatientGroup isRepeatedName = patientGroupRepository.findIsNameRepeated(doctorId, name);
        if(null!=isRepeatedName&&StringUtils.isNoneBlank(isRepeatedName.getName())&&name.equals(isRepeatedName.getName())){
            throw new CommonException(1044,"分组名称不支持重复");
        }
        PatientGroup group = new PatientGroup();
        if(StringUtils.isNotBlank(id)){
            group.setId(Integer.parseInt(id));
            group.setUpdateTime(new Date());
            group.setCreateTime(group.getUpdateTime());
            group.setDoctorId(doctorId);
            group.setName(StringUtils.trim(name));
            patientGroupRepository.save(group);
            return "编辑分组成功";
        }else{
            int maxSort = patientGroupRepository.getMaxSortByDoctorId(doctorId);
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
        if("0".equals(isDefault)&&StringUtils.isNotBlank(isDefault)){
            throw new CommonException(1045,"默认分组不能删除"); 
        }
        PatientGroup one = patientGroupRepository.findOne(Integer.parseInt(id));
        one.setId(Integer.parseInt(id));
        one.setDelFlag("1");
        one.setUpdateTime(new Date());
        patientGroupRepository.save(one);
        signUserDoctorGroupRepository.updateDoctorGroup(Integer.parseInt(id));
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
    public void addUserToGroup(List<String> groupIds, String userId) {
        //已分组的id
        List<Integer> list = signUserDoctorGroupRepository.getGroupIdsByUserId(userId);
        //传入的分组id
        List<Integer> list2 = CollStringToIntegerLst(groupIds);
        //取差集
        list2.removeAll(list);
        
        if(CollectionUtils.isNotEmpty(list2)){
            for(Integer groupId:list2){
                SignUserDoctorGroup signUserDoctorGroup = new SignUserDoctorGroup();
                signUserDoctorGroup.setGroupId(groupId);
                signUserDoctorGroup.setUid(userId);
                signUserDoctorGroup.setDelFlag("0");
                signUserDoctorGroupRepository.saveAndFlush(signUserDoctorGroup);
            }
        }
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
