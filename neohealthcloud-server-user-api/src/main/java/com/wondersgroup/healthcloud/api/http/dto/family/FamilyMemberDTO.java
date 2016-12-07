package com.wondersgroup.healthcloud.api.http.dto.family;

import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.api.http.dto.measure.SimpleMeasure;
import com.wondersgroup.healthcloud.services.user.dto.member.FamilyMemberInvitationAPIEntity;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberDTO {
    private List<FamilyMemberInvitationAPIEntity> invitsations;
    private List<MemberInfo> memberInfos;
    
    @Data
    public static class MemberInfo{
        private String name;
        private List<SimpleMeasure> measures;
        
        public MemberInfo() {
        }
        
        public MemberInfo(String name, List<SimpleMeasure> measures) {
            this.name = name;
            this.measures = measures;
        }
        
    }
    
}
