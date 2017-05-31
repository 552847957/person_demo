package com.wondersgroup.healthcloud.services.group;

import java.util.List;

import com.wondersgroup.healthcloud.jpa.entity.group.PatientGroup;

public interface PatientGroupService {
    
    public List<PatientGroup> getPatientGroupByDoctorId(String doctorId);
    
    public String savePatientGroup(String id,String doctorId,String name);
    
    public Boolean delPatientGroup(String id,String doctorId);
    
    public void sortPatientGroup(List<String> editIds,String doctorId);
    
    public int getGroupNmuByDoctorId(String doctorId);
    
    public void addUserToGroup(List<String> groupIds,String userId);
}
